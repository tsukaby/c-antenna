package com.tsukaby.c_antenna.client

import dispatch.Defaults._
import dispatch._
import org.json4s._
import org.json4s.native.JsonMethods._

trait HatenaClient {

  implicit val formats = DefaultFormats

  val limitOfHatebuCountApi = 50

  def getHatebuCounts(targetUrls: Seq[String]): Future[Seq[HatebuCount]] = {

    val groupedTargets: Seq[Seq[String]] = targetUrls.grouped(limitOfHatebuCountApi).toSeq

    val fResults = Future.sequence(for {
      urls <- groupedTargets
    } yield {
        val apiUrl = "http://api.b.st-hatena.com/entry.counts?url=" + urls.mkString("&url=")
        val request = url(apiUrl)
        val result = Http(request OK as.String)
        result
      })

    fResults.map { results =>
      results.flatMap { result =>
        parse(result).extract[Map[String, Long]] map { entry =>
          HatebuCount(url = entry._1, entry._2)
        }
      }
    }
  }
}

object HatenaClient extends HatenaClient

case class HatebuCount(
  url: String,
  count: Long
)