package com.tsukaby.c_antenna.service

import java.io.{Reader, InputStreamReader, BufferedReader}
import java.net.URL

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import com.tsukaby.c_antenna.dao.{ArticleDao, RssDao, SiteDao, SiteSummaryDao}
import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{Site, SitePage}
import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcClientConfigImpl}
import org.joda.time.DateTime
import scalikejdbc.{AutoSession, DBSession}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scalaz.Scalaz._

trait SiteService extends BaseService {

  val siteSummaryDao: SiteSummaryDao = SiteSummaryDao
  val siteDao: SiteDao = SiteDao
  val rssDao: RssDao = RssDao
  val articleDao: ArticleDao = ArticleDao

  /**
   * 引数で指定した検索条件に従ってサイトを取得します。
   * @param condition 条件
   * @return サイトの一覧
   */
  def getByCondition(condition: SimpleSearchCondition)(implicit session: DBSession = AutoSession): SitePage = {
    val sites = siteSummaryDao.getByCondition(condition)
    val count = siteSummaryDao.countByCondition(condition)

    SitePage(sites, count)
  }


  /**
   * 全てのサイトの情報を取得します。
   * RSS記事情報は最新の5件のみ取得します。
   *
   * @return
   */
  def getAll(implicit session: DBSession = AutoSession): Seq[Site] = {
    siteDao.getAll map (x => dbSitesToSites(x, articleDao.getLatelyBySiteId(x.id)))
  }

  /**
   * 引数で指定したサイトを取得します。
   * @param id サイトID
   * @return サイト
   */
  def getById(id: Long)(implicit session: DBSession = AutoSession): Option[Site] = {
    siteDao.getById(id) match {
      case Some(x) => x
      case None => None
    }
  }

  /**
   * 引数で指定したWebサイトをクロールし、最新RSSから記事の情報を集めます。
   * 集めた記事情報はデータストアに保存します。
   * @param site クロール対象サイト
   * @param session DBセッション
   */
  def crawl(site: SiteMapper)(implicit session: DBSession = AutoSession): Unit = {

    Logger.info(s"サイト情報を更新します。${site.name}")

    rssDao.getByUrl(site.rssUrl) match {
      case Some(feed) =>
        // サイト情報更新
        feed.getEntries.asScala.par foreach {
          // RSS記事URL更新
          case (entry: SyndEntry) =>

            if (new DateTime(entry.getPublishedDate).isBefore(new DateTime().plusHours(1))) {
              // RSS記事の日付が現在日時+1時間より前に作成されたものであればDB格納
              // +1は多少未来の投稿時間でも許容する為。
              // この処理は投稿日を未来設定して広告として利用している記事を排除する為の処理

              if (entry.getLink != null && articleDao.countByUrl(entry.getLink) == 0) {
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
                articleDao.create(site.id, entry.getLink, entry.getTitle, tags, 0, new DateTime(entry.getPublishedDate))
              }
            }
        }
      case None =>
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

  /**
   * 全てのサイトのRSSを取得し、サイト名を最新の状態にします。
   */
  def refreshSiteName(implicit session: DBSession = AutoSession): Unit = {
    siteDao.getAll foreach { x =>

      rssDao.getByUrl(x.rssUrl) match {
        case Some(feed) =>
          x.copy(name = feed.getTitle).save()
        case None =>
      }
    }
  }

  /**
   * サイトのランクを更新します。
   */
  def refreshSiteRank(implicit session: DBSession = AutoSession): Unit = {
    siteDao.getAll foreach { x =>
      // クライアント設定作成
      val conf = new XmlRpcClientConfigImpl()
      conf.setServerURL(new URL("http://b.hatena.ne.jp/xmlrpc"))
      // XML-RPCクライアント生成
      val client = new XmlRpcClient()
      // クライアント設定をセット
      client.setConfig(conf)
      // パラメータ作成
      // 実行
      val ret = client.execute("bookmark.getTotalCount", List(x.url))

      siteDao.update(x.copy(hatebuCount = ret.toString.toLong))
    }
  }

  /**
   * サイトのサムネイルを更新します。
   */
  def refreshSiteThumbnail(implicit session: DBSession = AutoSession): Unit = {
    siteDao.getAll.par foreach { x =>
      if (x.thumbnail.isEmpty) {
        // サムネ未登録の場合、登録
        val image = WebScrapingService.getImage(x.url)
        println(x.name)
        siteDao.update(x.copy(thumbnail = image.some))
      }
    }
  }
}

object SiteService extends SiteService
