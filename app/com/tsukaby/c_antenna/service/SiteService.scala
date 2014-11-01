package com.tsukaby.c_antenna.service

import akka.actor.ActorSystem
import com.tsukaby.c_antenna.dao.{ArticleDao, RssDao, SiteDao}
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{SimpleSearchCondition, Site, SitePage}
import de.nava.informa.core.{ChannelIF, ItemIF, ParseException}
import org.joda.time.DateTime
import play.api.Logger
import scalikejdbc.DB
import spray.client.pipelining.{Get, sendReceive}
import spray.http.{HttpRequest, HttpResponse}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SiteService extends BaseService {

  /**
   * 引数で指定した検索条件に従ってサイトを取得します。
   * @param condition 条件
   * @return サイトの一覧
   */
  def getByCondition(condition: SimpleSearchCondition): SitePage = {
    val sites = SiteDao.getByCondition(condition: SimpleSearchCondition)
    val count = SiteDao.countAll

    SitePage(sites, count)
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
        h = RssDao.getByUrl(updatedSite.url + "index.rdf")
      } catch {
        case e: ParseException =>
          h = RssDao.getByUrl(updatedSite.url + "?xml")
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

  trait WebClient {
    def get(url: String): Future[HttpResponse]
  }

  // implementation of WebClient trait
  class SprayWebClient(implicit system: ActorSystem) extends WebClient {

    // create a function from HttpRequest to a Future of HttpResponse
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    // create a function to send a GET request and receive a string response
    def get(url: String): Future[HttpResponse] = {
      pipeline(Get(url))
    }
  }

  //TODO ここどうにかする 消す？
  def check = {
    // bring the actor system in scope
    implicit val system = ActorSystem()

    // create the client
    val webClient = new SprayWebClient()(system)


    SiteDao.getAll foreach { x =>
      // send GET request with absolute URI
      // val futureResponse = webClient.get(x.url)

      // wait for Future to complete

      /*
      futureResponse onComplete {
        case Success(response) =>
          x.copy(rssUrl = response.status.toString()).save()
        case Failure(error) =>
      }*/

      if (x.rssUrl == "") {
        println("START!!! " + x.url)
        val rssSaffix = if (x.url.contains("yahoo.co.jp")) {
          "rss.xml"
        } else if (x.url.contains("seesaa.net")) {
          "index20.rdf"
        } else if (x.url.contains("ameba.jp")) {
          "rss.html"
        } else if (x.url.contains("fc2.")) {
          "?xml"
        } else if (x.url.contains("blogspot.com")) {
          "feeds/posts/default?alt=rss"
        } else if (x.url.contains("livedoor") || x.url.contains("ldblog") || x.url.contains("doorblog")) {
          "index.rdf"
        } else {
          "rss.xml"
          // "feed/atom"
        }

        if (rssSaffix != "") {
          val rssUrl = x.url + rssSaffix

          RssDao.getByUrl(rssUrl) match {
            case Some(rss) =>
              x.copy(name = rss.getTitle, rssUrl = rssUrl).save()
            case None =>
          }
        }
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

  /**
   * 全てのサイトのRSSを取得し、サイト名を最新の状態にします。
   */
  def refreshSiteName(): Unit = {
    DB localTx { implicit session =>
      SiteDao.getAll foreach { x =>

        RssDao.getByUrl(x.rssUrl) match {
          case Some(rss) =>
            x.copy(name = rss.getTitle).save()
          case None =>
        }
      }
    }
  }

}
