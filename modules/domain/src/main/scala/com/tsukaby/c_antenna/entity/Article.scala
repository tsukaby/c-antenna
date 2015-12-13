package com.tsukaby.c_antenna.entity

import org.joda.time.DateTime

/**
 * Webサイトの記事。主にblogの1記事を表現
 */
case class Article(
  id: Long,
  siteId: Long,
  url: String,
  eyeCatchUrl: Option[String],
  title: String,
  description: Option[String],
  tags: Seq[String],
  siteName: String,
  clickCount: Long,
  hatebuCount: Long,
  publishedAt: DateTime
)
