package com.tsukaby.c_antenna.lambda

import java.io.{InputStream, OutputStream}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import dispatch._
import dispatch.Defaults._

import scala.concurrent.Await
import scala.io.{Codec, Source}
import scala.concurrent.duration._
import scala.language.postfixOps

case class RssUrlFindRequest(pageUrl: String)

case class RssUrlFindResponse(rssUrl: String)

class Rss {
  implicit val formats = DefaultFormats

  def findRssUrl(input: InputStream, output: OutputStream): Unit = {
    val req = parse(Source.fromInputStream(input)(Codec.UTF8).mkString).extract[RssUrlFindRequest]
    val response = RssUrlFindResponse(rssUrl(req.pageUrl).orNull)
    val responseStr = Serialization.write(response)

    val result = responseStr.getBytes("UTF-8")
    output.write(result)
  }

  private def rssUrl(pageUrl: String): Option[String] = {
    val rssLinkReg = """<link.*?application/(rss|atom)\+xml.*?>""".r
    val hrefReg = """href\s*=\s*[\"|\'](.*?)[\"|\']""".r
    val urlReg = """[\"|\'](.*?)[\"|\']""".r

    val request = url(pageUrl)
    val pageF: Future[String] = Http.configure(_.setFollowRedirect(true))(request OK as.String)
    val html = Await.result(pageF, 30 seconds)

    for {
      linkTag <- rssLinkReg.findFirstMatchIn(html).map(_.toString())
      hrefAttribute <- hrefReg.findFirstMatchIn(linkTag).map(_.toString())
      urlWithQuote <- urlReg.findFirstMatchIn(hrefAttribute).map(_.toString())
      url = urlWithQuote.substring(1, urlWithQuote.length - 1)
    } yield url
  }
}
