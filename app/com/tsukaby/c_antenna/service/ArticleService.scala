package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{ArticlePage, SimpleSearchCondition}
import com.github.nscala_time.time.Imports._

/**
 * 記事に関する処理を行うクラスです。
 */
object ArticleService {

  /**
   * 検索条件にマッチする最新の記事を取得します。
   * @param condition 検索条件
   * @return 記事一覧
   */
  def getLately(condition: SimpleSearchCondition): ArticlePage = {

    val articles = ArticleDao.getByCondition(condition)
    val count = ArticleDao.countByCondition(condition)

    ArticlePage(articles, count)
  }

  /**
   * 検索条件にマッチするランキング付けされた記事を取得します。
   * @param condition 検索条件
   * @return 記事一覧
   */
  def getByRanking(condition: SimpleSearchCondition): ArticlePage = {

    val articles = ArticleDao.getByCondition(condition, 1.days)

    val count = ArticleDao.countByCondition(condition, 1.days)

    ArticlePage(articles, count)
  }
}
