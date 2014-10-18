package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import com.tsukaby.c_antenna.service.{WebScrapingService, MorphologicalService}
import de.nava.informa.core.{ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.joda.time.DateTime
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{Dimension, OutputType}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.collection.JavaConverters._

object Application extends Controller {

  def sample = Action {

    val rssUrl = "http://blog.livedoor.jp/itsoku/index.rdf"

    val channel: ChannelIF = Redis.get[ChannelIF](rssUrl) match {
      case Some(x) =>
        x
      case None =>
        val result = FeedParser.parse(new ChannelBuilder(), rssUrl)
        Redis.set(rssUrl, result, 60)
        result
    }

    println(channel.getTitle)
    println(channel.getSite.toString)

    channel.getItems.asScala foreach { case (x: ItemIF) =>
      println(x.getTitle)
      println(x.getLink)
    }
    Ok("")
  }

  def store = Action {
    val sites = SiteMapper.findAll()

    sites foreach (site => {
      getRss(site.rssUrl) match {
        case Some(channel) =>
          // サイト情報更新
          site.copy(name = channel.getTitle).save()

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

    Ok(Json.toJson(sites map (x => x.name)))
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

  private def getImage(url: String): Option[Array[Byte]] = {
    val driver: PhantomJSDriver = new PhantomJSDriver(new DesiredCapabilities())

    //driver.manage().window().setSize(new Dimension(1366, 768))
    driver.manage().window().setSize(new Dimension(400, 300))
    driver.get(url)
    val bytes: Array[Byte] = driver.getScreenshotAs(OutputType.BYTES)
    driver.quit()

    if (bytes == null || bytes.length == 0) {
      None
    } else {
      Option(bytes)
    }
  }

}
