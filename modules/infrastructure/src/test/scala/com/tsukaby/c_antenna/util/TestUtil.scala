package com.tsukaby.c_antenna.util

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.{SiteMapper, ArticleMapper}

import org.joda.time.DateTime

/**
 * テスト用の便利ツール
 */
object TestUtil {

  def getBaseCondition: SimpleSearchCondition = {
    SimpleSearchCondition(Some(1), Some(10), None, None, false, None, None, None)
  }

  def getBaseArticleMapper: ArticleMapper = {
    ArticleMapper(1, 1, "http://example.com", Some("http://example.com/foo.jpg"), "title", Some("description"), None, Some("tag"), 1, 1, DateTime.now)
  }

  def getBaseSiteMapper: SiteMapper = {
    SiteMapper(1L, "site_name", "http://", "http://rss", Some("http://example.com/1.jpg"), "css_selector", 1L, 1L, DateTime.now)
  }

}
