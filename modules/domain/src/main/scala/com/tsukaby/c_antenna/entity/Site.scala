package com.tsukaby.c_antenna.entity

/**
 * Webサイト
 */
case class Site(
                 id: Long,
                 name: String,
                 url: String,
                 recentArticles: Seq[Article])
