package com.tsukaby.c_antenna.dao

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.net.URL

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.feed.synd.SyndFeed

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * informaライブラリを利用してRSSを取得するクラスです。
 */
trait RssDao extends BaseDao {

  /**
   * 引数で指定したURLのRSSを取得します。
   * @param rssUrl 取得するRSS URL
   */
  def getByUrl(rssUrl: String): Future[Option[SyndFeed]] = {

    Future {
      val tmp = new URL(rssUrl)
      val conn = tmp.openConnection()
      // PC Chrome
      conn.setRequestProperty("User-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
      val reader: Reader = new BufferedReader(new InputStreamReader(conn.getInputStream))

      val in = new SyndFeedInput()
      val feed = in.build(reader)

      Some(feed)
    } recover {
      case e: IOException =>
        Logger.warn("Can't read contents.", e)
        None
    }
  }
}

object RssDao extends RssDao
