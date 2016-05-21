package com.tsukaby.c_antenna.dao

import java.io.{BufferedReader, InputStreamReader, Reader}
import java.net.URL

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      case e: Exception =>
        Logger.warn("Can't read feeds.", e)
        None
    }
  }
}

object RssDao extends RssDao
