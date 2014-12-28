package com.tsukaby.c_antenna.entity

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

case class SitePage(
                     items: Seq[Site],
                     total: Long
                     ) extends Page[Site]
