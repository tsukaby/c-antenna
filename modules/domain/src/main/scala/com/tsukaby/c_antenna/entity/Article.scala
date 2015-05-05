package com.tsukaby.c_antenna.entity

import org.joda.time.DateTime

/**
 * Webサイトの記事。主にblogの1記事を表現
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
