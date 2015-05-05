package com.tsukaby.c_antenna.entity

/**
 * クリックを表現するクラスです。
 */
case class ClickLog(
                     siteId: Option[Long],
                     articleId: Option[Long]
                     )
