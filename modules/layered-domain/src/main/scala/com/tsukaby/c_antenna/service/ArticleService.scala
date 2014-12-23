package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import com.tsukaby.c_antenna.entity.{ArticlePage, SimpleSearchCondition}
import scalikejdbc.{AutoSession, DBSession}

/**
 * 記事に関する処理を行うクラスです。
 */
trait ArticleService extends BaseService {

  val articleDao: ArticleDao = ArticleDao

  /**
   * 検索条件にマッチする記事を取得します。
   * @param condition 検索条件
   * @return 記事一覧
   */
  def getByCondition(condition: SimpleSearchCondition)(implicit session: DBSession = AutoSession): ArticlePage = {

    val articles = articleDao.getByCondition(condition)
    val count = articleDao.countByCondition(condition)

    ArticlePage(articles, count)
  }
}

object ArticleService extends ArticleService