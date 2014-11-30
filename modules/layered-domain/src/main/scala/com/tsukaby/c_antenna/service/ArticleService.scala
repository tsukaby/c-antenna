package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{ArticlePage, SimpleSearchCondition}

/**
 * 記事に関する処理を行うクラスです。
 */
trait ArticleService {

  /**
   * 検索条件にマッチする記事を取得します。
   * @param condition 検索条件
   * @return 記事一覧
   */
  def getByCondition(condition: SimpleSearchCondition): ArticlePage = {

    val articles = ArticleDao.getByCondition(condition)
    val count = ArticleDao.countByCondition(condition)

    ArticlePage(articles, count)
  }
}

object ArticleService extends ArticleService