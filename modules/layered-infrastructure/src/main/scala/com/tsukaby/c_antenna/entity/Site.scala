package com.tsukaby.c_antenna.entity

case class Site(
                 id: Long,
                 name: String,
                 url: String,
                 thumbnail: Option[String],
                 recentArticles: Seq[Article])
