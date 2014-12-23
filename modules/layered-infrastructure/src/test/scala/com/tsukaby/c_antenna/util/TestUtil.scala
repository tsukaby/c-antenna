package com.tsukaby.c_antenna.util

import com.tsukaby.c_antenna.db.mapper.{SiteMapper, SiteSummaryMapper, ArticleMapper}
import com.tsukaby.c_antenna.entity.{Site, Article, ClickLog, SimpleSearchCondition}
import org.joda.time.DateTime

import scalaz.Scalaz._

/**
 * テスト用の便利ツール
 */
object TestUtil {

  def getBaseCondition: SimpleSearchCondition = {
    SimpleSearchCondition(1.some, 10.some, none, none, none)
  }

  def getBaseClickLog: ClickLog = {
    ClickLog(1L.some, 1L.some)
  }

  def getBaseArticle: Article = {
    Article(1, 1, "http://example.com", "title", "thumb", Seq("tag"), "site_name", DateTime.now, 1)
  }

  def getBaseArticleMapper: ArticleMapper = {
    ArticleMapper(1, 1, "http://example.com", "title", "tag".some, 1, DateTime.now)
  }

  def getBaseSite: Site = {
    Site(1, "site_name", "http://", "thumb".some, Seq(getBaseArticle))
  }

  def getBaseSiteMapper: SiteMapper = {
    SiteMapper(1L, "site_name", "http://", "http://rss", Array[Byte]().some, "css_selector", 1L, 1L, DateTime.now)
  }

  def getBaseSiteSummaryMapper = {
    SiteSummaryMapper(1L, "site_name", "http://", Array[Byte]().some, 1L, 1L, 1L)
  }
}
