package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import org.joda.time.DateTime

/**
 * テスト用の便利ツール
 */
object TestUtil {

  def getBaseCondition: SimpleSearchCondition = {
    SimpleSearchCondition(Some(1), Some(10), None, None, false, None, None, None)
  }

  def getBaseClickLog: ClickLog = {
    ClickLog(Some(1L), Some(1L))
  }

  def getBaseArticle: Article = {
    Article(1, 1, "http://example.com", Some("http://example.com/foo.jpg"), "title", Some("description"), Seq("tag"), "site_name", 1, 1, DateTime.now)
  }

  def getBaseArticleMapper: ArticleMapper = {
    ArticleMapper(1, 1, "http://example.com", Some("http://example.com/foo.jpg"), "title", Some("description"), None, Some("tag"), 1, 1, DateTime.now)
  }

  def getBaseSite: Site = {
    Site(1, "site_name", "http://", Some("http://example.com/1.jpg"), 1, 1, Seq(getBaseArticle))
  }

  def getBaseSiteMapper: SiteMapper = {
    SiteMapper(1L, "site_name", "http://", "http://rss", Some("http://example.com/1.jpg"), "css_selector", 1L, 1L, DateTime.now, false)
  }
}
