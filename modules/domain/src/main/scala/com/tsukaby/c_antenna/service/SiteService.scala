package com.tsukaby.c_antenna.service

import java.io.ByteArrayInputStream
import java.net.URL

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.{TransferManager, TransferManagerConfiguration}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.rometools.rome.feed.synd.SyndEntry
import com.tsukaby.c_antenna.dao.{ArticleDao, CategoryDao, RssDao, SiteDao}
import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{Site, SitePage}
import com.tsukaby.c_antenna.lambda.{AnalyzeRequest, ClassificationRequest, LambdaInvoker, RssUrlFindRequest}
import com.typesafe.config.ConfigFactory
import kamon.Kamon
import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcClientConfigImpl}
import org.joda.time.DateTime
import scalikejdbc._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.language.reflectiveCalls
import scala.util.{Success, Failure}

trait SiteService extends BaseService {

  val config = ConfigFactory.load()
  val imageBucket = config.getString("c-antenna.s3.image-bucket")

  val siteDao: SiteDao = SiteDao
  val rssDao: RssDao = RssDao
  val articleDao: ArticleDao = ArticleDao
  val categoryDao: CategoryDao = CategoryDao

  val s3client: AmazonS3 = {
    val tmp = new AmazonS3Client()
    tmp.setEndpoint(config.getString("c-antenna.s3.end-point"))
    tmp
  }

  val manager: TransferManager = {
    val tmp = new TransferManager(s3client)
    val conf = new TransferManagerConfiguration()
    conf.setMinimumUploadPartSize(10 * 1024 * 1024)
    tmp.setConfiguration(conf)
    tmp
  }

  private lazy val counters = new {
    val addedNewSite = Kamon.metrics.counter("add-new-site")
    val addedNewArticle = Kamon.metrics.counter("add-new-article")
  }

  /**
    * 引数で指定した検索条件に従ってサイトを取得します。
    * @param condition 条件
    * @return サイトの一覧
    */
  def getByCondition(condition: SimpleSearchCondition)(implicit session: DBSession = AutoSession): SitePage = {
    val sites = siteDao.getByCondition(condition)
    val count = siteDao.countByCondition(condition)

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

  val hatenaRssUrls: List[String] = List(
    "http://feeds.feedburner.com/hatena/b/hotentry",
    "http://b.hatena.ne.jp/entrylist.rss",
    "http://b.hatena.ne.jp/hotentry/social.rss",
    "http://b.hatena.ne.jp/entrylist/social.rss",
    "http://b.hatena.ne.jp/hotentry/economics.rss",
    "http://b.hatena.ne.jp/entrylist/economics.rss",
    "http://b.hatena.ne.jp/hotentry/life.rss",
    "http://b.hatena.ne.jp/entrylist/life.rss",
    "http://b.hatena.ne.jp/hotentry/knowledge.rss",
    "http://b.hatena.ne.jp/entrylist/knowledge.rss",
    "http://b.hatena.ne.jp/hotentry/it.rss",
    "http://b.hatena.ne.jp/entrylist/it.rss",
    "http://b.hatena.ne.jp/hotentry/entertainment.rss",
    "http://b.hatena.ne.jp/entrylist/entertainment.rss",
    "http://b.hatena.ne.jp/hotentry/game.rss",
    "http://b.hatena.ne.jp/entrylist/game.rss",
    "http://b.hatena.ne.jp/hotentry/fun.rss",
    "http://b.hatena.ne.jp/entrylist/fun.rss",
    "http://feeds.feedburner.com/hatena/b/video"
  )

  /**
    * Hatenaのエントリーリストをクロールし、新しくクロールするサイトを集めます。
    * 集めたサイトはDBに保存します。
    * @param session DBセッション
    */
  def crawlNewSite(implicit session: DBSession = AutoSession): Future[Unit] = {

    Logger.info(s"新しいサイトを収集します。")

    val f = Future.sequence(hatenaRssUrls.map(rssDao.getByUrl)).map(x => x.flatMap(_.getEntries.asScala))
      .map { entries =>
        entries.foreach { entry =>
            val rssUrl: Option[String] = Option(LambdaInvoker().findRssUrl(new RssUrlFindRequest(entry.getLink)).getRssUrl)
            rssUrl.foreach { url =>
              rssDao.getByUrl(url).foreach { feed2 =>
                if (siteDao.getByUrl(feed2.getLink).isEmpty) {
                  siteDao.create(
                    name = feed2.getTitle,
                    url = feed2.getLink,
                    rssUrl = url,
                    scrapingCssSelector = "",
                    clickCount = 0,
                    hatebuCount = 0,
                    crawledAt = DateTime.now
                  )
                  Logger.info(s"Inserted a site. title = ${feed2.getTitle}")
                  counters.addedNewSite.increment()
                }
              }
            }
        }
      }

    f.onComplete {
      case Success(x) => Logger.info(s"新しいサイトを収集が完了しました。")
      case Failure(e) => Logger.info(s"新しいサイトの収集中にエラーが発生しました。", e)
    }

    f
  }

  /**
    * 引数で指定したWebサイトをクロールし、最新RSSから記事の情報を集めます。
    * 集めた記事情報はデータストアに保存します。
    * @param site クロール対象サイト
    * @param session DBセッション
    */
  def crawl(site: SiteMapper)(implicit session: DBSession = AutoSession): Future[Unit] = {

    Logger.debug(s"サイト情報を更新します。${site.name}")

    rssDao.getByUrl(site.rssUrl).map { feed =>
      // サイト情報更新
      feed.getEntries.asScala.par foreach {
        // RSS記事URL更新
        case (entry: SyndEntry) =>

          if (new DateTime(entry.getPublishedDate).isBefore(new DateTime().plusHours(1))) {
            // RSS記事の日付が現在日時+1時間より前に作成されたものであればDB格納
            // +1は多少未来の投稿時間でも許容する為。
            // この処理は投稿日を未来設定して広告として利用している記事を排除する為の処理

            if (entry.getLink != null && articleDao.countByUrl(entry.getLink) == 0) {
              // まだ記事が無い場合
              // 記事を解析してタグを取得
              val tags = LambdaInvoker().analyzeMorphological(new AnalyzeRequest(entry.getDescription.getValue)).tags
              val categoryName = LambdaInvoker().classifyCategory(new ClassificationRequest(tags)).category
              val category = categoryDao.getByName(categoryName)

              val content = entry.getContents.headOption.map(_.getValue)
              val eyeCatchUrl = imageUrl(content)

              // DB登録
              articleDao.create(
                siteId = site.id,
                url = entry.getLink,
                eyeCatchUrl = eyeCatchUrl,
                title = entry.getTitle,
                description = Some(entry.getDescription.getValue),
                categoryId = category.map(_.id),
                tags = if (tags.nonEmpty) Some(tags.mkString(",").take(1024)) else None,
                clickCount = 0,
                hatebuCount = 0,
                publishedAt = new DateTime(entry.getPublishedDate)
              )
              Logger.info(s"Inserted an article. title = ${entry.getTitle}")
              counters.addedNewArticle.increment()
            }
          }
      }
    }
  }

  private def imageUrl(htmlOpt: Option[String]): Option[String] = {
    val imageReg = """<img.*?src\s*=\s*[\"|\'](.*?)[\"|\']>""".r
    val srcReg = """src\s*=\s*[\"|\'](.*?)[\"|\']""".r
    val urlReg = """[\"|\'](.*?)[\"|\']""".r

    for {
      html <- htmlOpt
      imageTag <- imageReg.findFirstMatchIn(html).map(_.toString())
      srcAttribute <- srcReg.findFirstMatchIn(imageTag).map(_.toString())
      urlWithQuote <- urlReg.findFirstMatchIn(srcAttribute).map(_.toString())
      url = urlWithQuote.substring(1, urlWithQuote.length - 1)
    } yield url
  }

  /**
    * サイトのRSSを取得し、サイト名を最新の状態にします。
    *
    * @param site サイト名更新対象サイト
    */
  def refreshSiteName(site: SiteMapper)(implicit session: DBSession = AutoSession): Future[Unit] = {
    rssDao.getByUrl(site.rssUrl).map { feed =>
      site.copy(name = feed.getTitle).save()
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
  def createSiteThumbnails(implicit session: DBSession = AutoSession): Unit = {
    SiteMapper.findAllBy(sqls.isNull(SiteMapper.sm.thumbnailUrl)) foreach { x =>
      val image: Array[Byte] = WebScrapingService.getImage(x.url)
      Logger.info(s"Site thumbnail created by web scraping. url = ${x.url}")

      val bais = new ByteArrayInputStream(image)
      val putMetaData = new ObjectMetadata()
      putMetaData.setContentLength(image.length)

      val fileName = s"${x.id}.jpg"
      val key = s"image/site_thumbs/$fileName"

      try {
        val upload = manager.upload(imageBucket, key, bais, putMetaData)
        upload.waitForCompletion()
        Logger.info(s"Thumbnail image uploaded. id = ${x.id}, url = ${x.url}")
        siteDao.update(x.copy(thumbnailUrl = Some(s"http://$imageBucket/$key")))
      } catch {
        case e: Throwable => Logger.error("Error s3 uploading.", e)
      }

      bais.close()

    }
  }
}

object SiteService extends SiteService
