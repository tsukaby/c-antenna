package com.tsukaby.c_antenna.entity

import play.api.libs.json.Json

case class Site(
                 id: Long,
                 name: String,
                 url: String,
                 thumbnail: Option[String],
                 recentArticles: Seq[Article])

object Site {
  implicit val format = Json.format[Site]
}
