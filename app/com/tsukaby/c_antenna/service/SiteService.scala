package com.tsukaby.c_antenna.service

import java.net.URL

import play.api.Logger
import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.Site
import de.nava.informa.core.{ParseException, ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.joda.time.DateTime

import scala.collection.JavaConverters._

object SiteService extends BaseService {

  def getWithPaging(page: Int, count: Int): Seq[Site] = {
    val sites = SiteDao.getWithPaging(page, count)
    sites map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  /**
   * 全てのサイトの情報を取得します。
   * RSS記事情報は最新の5件のみ取得します。
   *
   * @return
   */
  def getAll: Seq[Site] = {
    SiteDao.getAll map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  /**
   * 引数で指定したサイトを取得します。
   * @param id サイトID
   * @return サイト
   */
  def getById(id: Long): Option[Site] = {
    SiteDao.getById(id) match {
      case Some(x) => x
      case None => None
    }
  }

  def crawl: Unit = {
    val sites = SiteDao.getOldCrawledSite(10)

    sites foreach (site => {

      val updatedSite = SiteDao.update(site.copy(crawledAt = new DateTime()))

      // index.rdfか?xmlを対象 TODO できればどんなサイトでも対応できるように
      var h: Option[ChannelIF] = None
      try {
        h = getRss(updatedSite.url + "index.rdf")
      } catch {
        case e: ParseException =>
          h = getRss(updatedSite.url + "?xml")
      }

      h match {
        case Some(channel) =>
          // サイト情報更新
          Logger.info(s"サイト情報を更新します。${updatedSite.name}")
          SiteDao.update(updatedSite.copy(name = channel.getTitle))

          channel.getItems.asScala foreach {
            // RSS記事URL更新
            case (item: ItemIF) =>

              if (new DateTime(item.getDate).isBefore(new DateTime().plusHours(1))) {
                // RSS記事の日付が現在日時+1時間より前に作成されたものであればDB格納
                // +1は多少未来の投稿時間でも許容する為。
                // この処理は投稿日を未来設定して広告として利用している記事を排除する為の処理

                ArticleDao.getById(item.getLink.toString) match {
                  case Some(x) => //対象記事が見つかった場合は既に登録されているので無視
                  case None =>
                    //まだ記事が無い場合

                    // 記事を解析してタグを取得
                    //val tmp = getTags(item.getLink.toString, site.scrapingCssSelector)
                    val tmp = Seq[(String, Int)]()
                    val tags = if (tmp.length == 0) {
                      None
                    } else {
                      Option(tmp map (x => x._1) reduceLeft (_ + " " + _))
                    }
                    // DB登録
                    ArticleDao.create(item.getLink.toString, updatedSite.id, item.getTitle, tags, new DateTime(item.getDate))
                }
              }
          }
        case None =>
      }
    })
  }

  private def getRss(rssUrl: String): Option[ChannelIF] = {
    Redis.get[ChannelIF](rssUrl) match {
      case Some(x) =>
        Option(x)
      case None =>
        // 403で弾かれることが多い為、User-agentを指定して極力回避
        val feedUrl = new URL(rssUrl)
        val conn = feedUrl.openConnection
        conn.setRequestProperty("User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36")
        try {
          val result = FeedParser.parse(new ChannelBuilder(), conn.getInputStream)
          if (result == null) {
            None
          } else {
            Redis.set(rssUrl, result, 60)
            Option(result)
          }
        } catch {
          case e: Exception =>
            Logger.warn(e.getMessage)
            None
        }

    }
  }

  private def getTags(articleUrl: String, cssSelector: String): Seq[(String, Int)] = {
    val str = WebScrapingService.getText(articleUrl, cssSelector)

    str match {
      case Some(x) =>
        // 上位10個のみ対象記事のタグとして認める
        MorphologicalService.getTags(x).take(10)
      case None =>
        Seq()
    }
  }
}
