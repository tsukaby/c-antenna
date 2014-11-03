package com.tsukaby.c_antenna.entity

import play.api.libs.json.Json

/**
 * クリックを表現するクラスです。
 */
case class ClickLog(siteId: Option[Long], articleId: Option[Long])

object ClickLog {
  implicit val format = Json.format[ClickLog]
}
