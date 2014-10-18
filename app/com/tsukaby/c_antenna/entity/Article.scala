package com.tsukaby.c_antenna.entity

import play.api.libs.json.Json

/**
 * 記事を表現します。
 * thumbnailはBase64エンコードされた画像ファイルです。
 */
case class Article(
                    url: String,
                    title: String,
                    thumbnail: String,
                    tags: Seq[String])

object Article {
  implicit val format = Json.format[Article]
}