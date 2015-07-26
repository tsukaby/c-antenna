package com.tsukaby.c_antenna.client

import dispatch.Defaults._
import dispatch._
import org.json4s._
import org.json4s.native.JsonMethods._

trait TwitterClient {

  implicit val formats = DefaultFormats

  def getTweetCount(target: String): Future[TweetCount] = {
    val request = url("http://urls.api.twitter.com/1/urls/count.json?url=" + target)
    val count = Http(request OK as.String)
    count.map { x =>
      parse(x).extract[TweetCount].copy(url = target)
    }
  }

}

object TwitterClient extends TwitterClient

case class TweetCount(
  url: String,
  count: Long
)
