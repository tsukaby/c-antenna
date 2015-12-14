package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.client.HatenaClient
import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import com.tsukaby.c_antenna.entity.ArticlePage
import com.tsukaby.c_antenna.entity.ImplicitConverter._
import org.joda.time.DateTime
import scalikejdbc.{AutoSession, DBSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
 * 記事に関する処理を行うクラスです。
 */
trait ArticleService extends BaseService {

  val articleDao: ArticleDao = ArticleDao
  val hatenaClient: HatenaClient = HatenaClient

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

  def refreshRecentArticleRank(implicit session: DBSession = AutoSession): Unit = {
    val now = DateTime.now
    val condition = SimpleSearchCondition(
      page = None,
      count = None,
      maxId = None,
      categoryId = None,
      hasEyeCatch = false,
      startDateTime = Some(now.minusWeeks(1)),
      endDateTime = Some(now),
      sort = None)
    articleDao.getByCondition(condition) foreach { article =>
      refreshArticleRank(article)
    }
  }

  def refreshArticleRank(implicit session: DBSession = AutoSession): Unit = {
    articleDao.getAll foreach { article =>
      refreshArticleRank(article)
    }
  }

  private def refreshArticleRank(article: ArticleMapper) = {
    val f1 = hatenaClient.getHatebuCounts(article.url :: Nil).map(_.head)

    for {
      hatebuCount <- f1
    } {
      Logger.info(f"hatebuCount = ${hatebuCount.count}, url = ${article.url}")
      articleDao.update(article.copy(hatebuCount = hatebuCount.count))
    }
  }
}

object ArticleService extends ArticleService
