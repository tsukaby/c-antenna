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
import com.github.nscala_time.time.Imports._
import de.l3s.boilerpipe.extractors.DefaultExtractor

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

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
    *
    * @param condition 条件
    * @return サイトの一覧
    */
  def getByCondition(condition: SimpleSearchCondition)(implicit session: DBSession = AutoSession): SitePage = {
    val sites = siteDao.getByCondition(condition)
    val count = 1000

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
    *
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
    *
    * @param session DBセッション
    */
  def crawlNewSite(implicit session: DBSession = AutoSession): Future[Unit] = {

    Logger.info(s"新しいサイトを収集します。")

    val f = Future.sequence(hatenaRssUrls.map(rssDao.getByUrl)).map(x => x.flatten.flatMap(_.getEntries.asScala))
      .map { entries =>
        entries.foreach { entry =>
          val rssUrl: Option[String] = Try(LambdaInvoker().findRssUrl(new RssUrlFindRequest(entry.getLink))) match {
            case Failure(exception) =>
              Logger.warn("Failure lambda invoke.", exception)
              None
            case Success(value) =>
              if (value.getRssUrl == null) {
                Logger.warn("Lambda succeeded but return is null")
              }
              Option(value.getRssUrl)
          }
          rssUrl.foreach { url =>
            rssDao.getByUrl(url).foreach { feed2Opt =>
              feed2Opt.foreach { feed2 =>
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
    *
    * @param site クロール対象サイト
    * @param session DBセッション
    */
  def crawl(site: SiteMapper)(implicit session: DBSession = AutoSession): Future[Unit] = {

    Logger.debug(s"サイト情報を更新します。${site.name}")

    rssDao.getByUrl(site.rssUrl).map { feedOpt =>
      feedOpt.foreach { feed =>
        // サイト情報更新
        feed.getEntries.asScala.par foreach {
          // RSS記事URL更新
          case (entry: SyndEntry) =>

            val publishedDate = new DateTime(entry.getPublishedDate)
            val now = DateTime.now
            val pastOneMonth = DateTime.now - 1.month

            if (publishedDate < now && publishedDate > pastOneMonth) {
              // 投稿日が現在日時よりも前かつ1ヶ月以内
              // 広告や固定投稿はかなり先の未来に設定されているため、１つめの条件で除外

              if (entry.getLink != null && articleDao.countByUrl(entry.getLink) == 0) {
                // まだ記事が無い場合
                // 記事を解析してタグを取得
                val text: String = fetchContentTextOfWebPage(site.rssUrl).take(16384)
                val tags = LambdaInvoker().analyzeMorphological(new AnalyzeRequest(text)).tags
                val categoryName = LambdaInvoker().classifyCategory(new ClassificationRequest(tags)).category
                val category = categoryDao.getByName(categoryName)

                val content = entry.getContents.headOption.map(_.getValue)
                val eyeCatchUrl = imageUrl(content)

                // DB登録
                try {
                  articleDao.create(
                    siteId = site.id,
                    url = entry.getLink,
                    eyeCatchUrl = eyeCatchUrl,
                    title = entry.getTitle.replaceAll("\r\n", "").replaceAll("\n", ""),
                    description = Some(text),
                    categoryId = category.map(_.id),
                    tags = if (tags.nonEmpty) Some(tags.mkString(",").take(1024)) else None,
                    clickCount = 0,
                    hatebuCount = 0,
                    publishedAt = new DateTime(entry.getPublishedDate)
                  )
                  Logger.info(s"Inserted an article. title = ${entry.getTitle}")
                } catch {
                  case e : Exception =>
                    Logger.warn("Error article can't inserted.", e)
                }
                counters.addedNewArticle.increment()
              }
            }
        }
      }
    }
  }

  private def fetchContentTextOfWebPage(targetUrl: String): String = {
    val url = new URL(targetUrl)
    val conn = url.openConnection()
    // Avoid http response 403
    // UA as PC Chrome
    conn.setRequestProperty("User-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
    val plainContent: String = Source.fromInputStream(conn.getInputStream).mkString
    DefaultExtractor.getInstance().getText(plainContent)
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
    rssDao.getByUrl(site.rssUrl).map { feedOpt =>
      feedOpt.map { feed =>
        site.copy(name = feed.getTitle).save()
      }
    }
  }

  /**
    * サイトのランクを更新します。
    */
  def refreshSiteRank(implicit session: DBSession = AutoSession): Unit = {
    siteDao.getAll foreach { x =>
      try {
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
      } catch {
        case e: Exception =>
          Logger.warn(s"Error can't update ranking of site. siteId = ${x.id}", e)
      }
    }
  }

  /**
    * サイトのサムネイルを更新します。
    */
  def createSiteThumbnails(implicit session: DBSession = AutoSession): Unit = {
    SiteMapper.findAllBy(sqls.isNull(SiteMapper.sm.thumbnailUrl)) foreach { x =>
      val image: Array[Byte] = WebScrapingService.getImage(x.url)
      if (image.nonEmpty) {
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
}

object SiteService extends SiteService
