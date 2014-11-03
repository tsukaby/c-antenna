package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{ArticlePage, SimpleSearchCondition}

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
    val count = ArticleDao.countAll

    ArticlePage(articles, count)
  }

  /**
   * 検索条件にマッチするランキング付けされた記事を取得します。
   * @param condition 検索条件
   * @return 記事一覧
   */
  def getByRanking(condition: SimpleSearchCondition): ArticlePage = {

    val start = (condition.page.get - 1) * condition.count.get
    val end = start + condition.count.get

    val articleIds: Set[Long] = Redis.zrevrange("articleRanking", start, end) map (x => x.toLong)

    val articles = articleIds map { x =>
      ArticleDao.getById(x)
    } collect {
      case x if x.isDefined => x.get
    } toSeq

    val count = ArticleDao.countAll

    ArticlePage(articles, count)
  }
}
