package com.tsukaby.c_antenna.entity

import play.api.libs.json.Json

/**
 * ページングして結果を返す場合のクラス
 * @tparam T ページングオブジェクト内の実体であるデータ型
 */
sealed trait Page[T] {
  val items: Seq[T]
  //ページ内のデータ
  val total: Long // ページングしない場合の全体の件数
}

case class ArticlePage(
                        items: Seq[Article],
                        total: Long
                        ) extends Page[Article]

object ArticlePage {
  implicit val format = Json.format[ArticlePage]
}

case class SitePage(
                     items: Seq[Site],
                     total: Long
                     ) extends Page[Site]

object SitePage {
  implicit val format = Json.format[SitePage]
}
