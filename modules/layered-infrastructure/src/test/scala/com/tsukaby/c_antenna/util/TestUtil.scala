package com.tsukaby.c_antenna.util

import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import com.tsukaby.c_antenna.entity.{Article, ClickLog, SimpleSearchCondition}
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
}
