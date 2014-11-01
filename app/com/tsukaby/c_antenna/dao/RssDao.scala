package com.tsukaby.c_antenna.dao

import java.net.URL

import com.tsukaby.c_antenna.Redis
import de.nava.informa.core.ChannelIF
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser

import scalaz.Scalaz._

/**
 * informaライブラリを利用してRSSを取得するクラスです。
 */
object RssDao {
  def getByUrl(rssUrl: String): Option[ChannelIF] = {

    // RSSは常に変化するのでキャッシュ時間は短めに設定
    Redis.getOrElse[Option[ChannelIF]](rssUrl, 60) {
      // 403で弾かれることが多い為、User-agentを指定して極力回避
      val feedUrl = new URL(rssUrl)
      val conn = feedUrl.openConnection
      conn.setRequestProperty("User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36")
      try {
        val result = FeedParser.parse(new ChannelBuilder(), conn.getInputStream)
        if (result == null) {
          none
        } else {
          Redis.set(rssUrl, result, 60)
          result.some
        }
      } catch {
        case e: Exception =>
          none
      }
    }
  }
}
