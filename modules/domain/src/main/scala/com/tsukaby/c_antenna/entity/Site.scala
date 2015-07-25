package com.tsukaby.c_antenna.entity

/**
 * Webサイト
 */
case class Site(
  id: Long,
  name: String,
  url: String,
  thumbnailUrl: Option[String],
  recentArticles: Seq[Article]
)
