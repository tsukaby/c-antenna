package com.tsukaby.c_antenna.entity

import org.joda.time.DateTime

/**
 * 記事を表現します。
 * thumbnailはBase64エンコードされた画像ファイルです。
 */
case class Article(
                    id: Long,
                    siteId: Long,
                    url: String,
                    title: String,
                    tags: Seq[String],
                    siteName: String,
                    createdAt: DateTime,
                    clickCount: Long)
