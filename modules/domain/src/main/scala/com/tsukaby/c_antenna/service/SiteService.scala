package com.tsukaby.c_antenna.service

import java.io.FileOutputStream
import java.net.URL

import com.rometools.rome.feed.synd.SyndEntry
import com.tsukaby.c_antenna.dao.{ArticleDao, RssDao, SiteDao}
import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{Site, SitePage}
import com.tsukaby.c_antenna.lambda.{AnalyzeRequest, ClassificationRequest, LambdaInvoker}
import org.apache.xmlrpc.client.{XmlRpcClient, XmlRpcClientConfigImpl}
import org.joda.time.DateTime
import scalikejdbc.{AutoSession, DBSession}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SiteService extends BaseService {

  val siteDao: SiteDao = SiteDao
  val rssDao: RssDao = RssDao
  val articleDao: ArticleDao = ArticleDao

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

  /**
   * 引数で指定したWebサイトをクロールし、最新RSSから記事の情報を集めます。
   * 集めた記事情報はデータストアに保存します。
   * @param site クロール対象サイト
   * @param session DBセッション
   */
  def crawl(site: SiteMapper)(implicit session: DBSession = AutoSession): Future[Unit] = {

    Logger.info(s"サイト情報を更新します。${site.name}")

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
              Logger.info(s"tags = ${tags.mkString(",")}")
              val category = LambdaInvoker().classifyCategory(new ClassificationRequest(tags)).category
              Logger.info(s"category = $category")

              val content = entry.getContents.headOption.map(_.getValue).getOrElse("")
              val reg = """src=\".*?[jpg|jpeg|png|gif|bmp]\"""".r
              val rm = reg.findFirstMatchIn(content)
              val eyeCatchUrl = rm.map(x => x.toString().substring(5, x.toString().length - 1))

              // DB登録
              articleDao.create(
                siteId = site.id,
                url = entry.getLink,
                eyeCatchUrl = eyeCatchUrl,
                title = entry.getTitle,
                description = Some(entry.getDescription.getValue),
                tags = None,
                clickCount = 0,
                hatebuCount = 0,
                tweetCount = 0,
                publishedAt = new DateTime(entry.getPublishedDate)
              )
            }
          }
      }
    }
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
  def refreshSiteThumbnail(implicit session: DBSession = AutoSession): Unit = {
    siteDao.getAll foreach { x =>
      val image: Array[Byte] = WebScrapingService.getImage(x.url)
      val out = new FileOutputStream(s"${x.id}.jpg")
      try {
        out.write(image)
      } finally {
        out.close()
      }
    }
  }
}

object SiteService extends SiteService
