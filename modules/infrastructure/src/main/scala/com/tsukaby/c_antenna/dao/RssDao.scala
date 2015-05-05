package com.tsukaby.c_antenna.dao

import java.io.{InputStreamReader, BufferedReader, Reader}
import java.net.URL

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.feed.synd.SyndFeed
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Failure
import scalaz.Scalaz._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * informaライブラリを利用してRSSを取得するクラスです。
 */
trait RssDao {

  /**
   * 引数で指定したURLのRSSを取得します。
   * @param rssUrl 取得するRSS URL
   */
  def getByUrl(rssUrl: String): Option[SyndFeed] = {

    val f = Future {
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

    Await.ready(f, Duration.Inf)
    f.value.get match {
      case Failure(exception) => throw exception
      case util.Success(value) => value
    }
  }
}

object RssDao extends RssDao
