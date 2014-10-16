package com.tsukaby.c_antenna.entity

import play.api.libs.json.Json

case class Site(
                 id: Long,
                 name: String,
                 url: String)

object Site {
  implicit val format = Json.format[Site]
}
