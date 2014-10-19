package com.tsukaby.c_antenna.service

import _root_.play.api.Logger
import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import com.tsukaby.c_antenna.entity.Site
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import de.nava.informa.core.{ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.joda.time.DateTime
import scala.collection.JavaConverters._

import scalikejdbc._

object SiteService extends BaseService {

  private val sm = SiteMapper.sm

  /**
   * 全てのサイトの情報を取得します。
   * RSS記事情報は最新の5件のみ取得します。
   *
   * @return
   */
  def getAll: Seq[Site] = {
    val targets = SiteMapper.findAll()

    targets map (x => dbSitesToSites(x, ArticleMapper.findAllBy(sqls.eq(ArticleMapper.am.siteId, x.id).orderBy(ArticleMapper.am.createdAt).desc.limit(5))))
  }

  def getById(id: Long): Option[Site] = {
    SiteMapper.find(id) match {
      case Some(x) => x
      case None => None
    }
  }

  def crawl: Unit = {
    val sites = SiteMapper.findAll().sortWith(_.crawledAt.getMillis < _.crawledAt.getMillis).take(1)

    sites foreach (site => {
      getRss(site.rssUrl) match {
        case Some(channel) =>
          // サイト情報更新
          Logger.info(s"サイト情報を更新します。${site.name}")
          site.copy(name = channel.getTitle, crawledAt = new DateTime()).save()

          channel.getItems.asScala foreach {
            // RSS記事URL更新
            case (item: ItemIF) =>
              ArticleMapper.find(item.getLink.toString) match {
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
                  ArticleMapper.create(item.getLink.toString, site.id, item.getTitle, tags, new DateTime(item.getDate))
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
