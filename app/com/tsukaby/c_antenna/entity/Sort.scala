package com.tsukaby.c_antenna.entity

import play.api.libs.json._

/**
 * ソートを表現するクラスです。
 */
case class Sort(key: String, order: SortOrder)

object Sort {
  implicit val format = Json.format[Sort]
}
