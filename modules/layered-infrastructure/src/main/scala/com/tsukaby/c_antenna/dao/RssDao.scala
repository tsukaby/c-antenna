package com.tsukaby.c_antenna.dao

import java.io.{InputStreamReader, BufferedReader, Reader}
import java.net.URL

import com.rometools.rome.io.SyndFeedInput
import de.nava.informa.impl.basic.Feed

import scalaz.Scalaz._



/**
 * informaライブラリを利用してRSSを取得するクラスです。
 */
trait RssDao {

  /**
   * 引数で指定したURLのRSSを取得します。
   * @param rssUrl 取得するRSS URL
   */
  def getByUrl(rssUrl: String): Option[Feed] = {
    try {
      val tmp = new URL(rssUrl)
      val conn = tmp.openConnection()
      val reader: Reader = new BufferedReader(new InputStreamReader(conn.getInputStream))

      val in = new SyndFeedInput()
      val feed = in.build(reader)

      feed.some
    } catch {
      case e: Exception =>
        None
    }

  }
}

object RssDao extends RssDao
