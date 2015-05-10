package com.tsukaby.c_antenna.util

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.{SiteMapper, ArticleMapper}

import org.joda.time.DateTime

import scalaz.Scalaz._

/**
 * テスト用の便利ツール
 */
object TestUtil {

  def getBaseCondition: SimpleSearchCondition = {
    SimpleSearchCondition(1.some, 10.some, none, none, none)
  }

  def getBaseArticleMapper: ArticleMapper = {
    ArticleMapper(1, 1, "http://example.com", "title", "tag".some, 1, DateTime.now)
  }

  def getBaseSiteMapper: SiteMapper = {
    SiteMapper(1L, "site_name", "http://", "http://rss", Array[Byte]().some, "css_selector", 1L, 1L, DateTime.now)
  }

}
