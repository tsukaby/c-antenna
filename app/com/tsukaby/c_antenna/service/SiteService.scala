package com.tsukaby.c_antenna.service

import java.net.URL

import com.tsukaby.c_antenna.dao.{SiteSummaryDao, ArticleDao, RssDao, SiteDao}
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{SimpleSearchCondition, Site, SitePage}
import de.nava.informa.core.ItemIF
import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcClientConfigImpl}
import org.joda.time.DateTime
import play.api.Logger
import scalikejdbc.DB
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scalaz.Scalaz._

object SiteService extends BaseService {
  /**
   * 引数で指定した検索条件に従ってサイトを取得します。
   * @param condition 条件
   * @return サイトの一覧
   */
  def getByCondition(condition: SimpleSearchCondition): SitePage = DB readOnly { implicit session =>
    val sites = SiteSummaryDao.getByCondition(condition)
    val count = SiteSummaryDao.countByCondition(condition)

    SitePage(sites, count)
  }


  /**
   * 全てのサイトの情報を取得します。
   * RSS記事情報は最新の5件のみ取得します。
   *
   * @return
   */
  def getAll: Seq[Site] = DB readOnly { implicit session =>
    SiteDao.getAll map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  /**
   * 引数で指定したサイトを取得します。
   * @param id サイトID
   * @return サイト
   */
  def getById(id: Long): Option[Site] = DB readOnly { implicit session =>
    SiteDao.getById(id) match {
      case Some(x) => x
      case None => None
    }
  }

  def crawl: Unit = {
    val sites = SiteDao.getAll

    sites.par foreach (site => {

      Logger.info(s"サイト情報を更新します。${site.name}")
      //val updatedSite = SiteDao.update(site.copy(crawledAt = new DateTime()))

      RssDao.getByUrl(site.rssUrl) match {
        case Some(channel) =>
          // サイト情報更新
          channel.getItems.asScala.par foreach {
            // RSS記事URL更新
            case (item: ItemIF) =>

              if (new DateTime(item.getDate).isBefore(new DateTime().plusHours(1))) {
                // RSS記事の日付が現在日時+1時間より前に作成されたものであればDB格納
                // +1は多少未来の投稿時間でも許容する為。
                // この処理は投稿日を未来設定して広告として利用している記事を排除する為の処理

                if (ArticleDao.countByUrl(item.getLink.toString) == 0) {
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
                  ArticleDao.create(site.id, item.getLink.toString, item.getTitle, tags, 0, new DateTime(item.getDate))
                }
              }
          }
        case None =>
      }
    })
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
  def refreshSiteName(): Unit = DB localTx { implicit session =>
    SiteDao.getAll foreach { x =>

      RssDao.getByUrl(x.rssUrl) match {
        case Some(rss) =>
          x.copy(name = rss.getTitle).save()
        case None =>
      }
    }
  }

  /**
   * サイトのランクを更新します。
   */
  def refreshSiteRank(): Unit = DB localTx { implicit session =>
    SiteDao.getAll foreach { x =>
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

      SiteDao.update(x.copy(hatebuCount = ret.toString.toLong))
    }
  }

  /**
   * サイトのサムネイルを更新します。
   */
  def refreshSiteThumbnail(): Unit = DB localTx { implicit session =>
    SiteDao.getAll.par foreach { x =>
      if (x.thumbnail.isEmpty) {
        // サムネ未登録の場合、登録
        val image = WebScrapingService.getImage(x.url)
        println(x.name)
        SiteDao.update(x.copy(thumbnail = image.some))
      }
    }
  }

}
