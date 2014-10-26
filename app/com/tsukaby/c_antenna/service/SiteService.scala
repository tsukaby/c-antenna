package com.tsukaby.c_antenna.service

import _root_.play.api.Logger
import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.Site
import de.nava.informa.core.{ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.joda.time.DateTime

import scala.collection.JavaConverters._

object SiteService extends BaseService {

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
    val sites = SiteDao.getOldCrawledSite(1)

    sites foreach (site => {
      getRss(site.rssUrl) match {
        case Some(channel) =>
          // サイト情報更新
          Logger.info(s"サイト情報を更新します。${site.name}")
          SiteDao.update(site.copy(name = channel.getTitle, crawledAt = new DateTime()))

          channel.getItems.asScala foreach {
            // RSS記事URL更新
            case (item: ItemIF) =>
              ArticleDao.getById(item.getLink.toString) match {
                case Some(x) => //対象記事が見つかった場合は既に登録されているので無視
                case None =>
                  //まだ記事が無い場合

                  // 記事を解析してタグを取得
                  val tmp = getTags(item.getLink.toString, site.scrapingCssSelector)
                  val tags = if (tmp.length == 0) {
                    None
                  } else {
                    Option(tmp map (x => x._1) reduceLeft (_ + " " + _))
                  }
                  // DB登録
                  ArticleDao.create(item.getLink.toString, site.id, item.getTitle, tags, new DateTime(item.getDate))
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
        val result = FeedParser.parse(new ChannelBuilder(), rssUrl)
        if (result == null) {
          None
        } else {
          Redis.set(rssUrl, result, 60)
          Option(result)
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
