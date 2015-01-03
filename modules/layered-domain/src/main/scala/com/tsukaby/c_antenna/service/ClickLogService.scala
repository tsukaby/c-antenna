package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.ClickLog
import scalikejdbc.{AutoSession, DBSession}

trait ClickLogService extends BaseService {

  val articleDao:ArticleDao = ArticleDao
  val siteDao:SiteDao = SiteDao

  def storeClickLog(clickLog: ClickLog) {
    clickLog.siteId match {
      case Some(x) =>
        // サイトのクリックカウントを１つ上げる
        VolatilityCache.zincrby("siteRanking", 1, x.toString)
      case None =>
    }

    clickLog.articleId match {
      case Some(x) =>
        // 記事のクリックカウントを１つ上げる
        VolatilityCache.zincrby("articleRanking", 1, x.toString)
      case None =>
    }
  }

  def refreshRanking(implicit session: DBSession = AutoSession): Unit = {
    synchronized {
      VolatilityCache.zrevrange("siteRanking", 0, VolatilityCache.zcard("siteRanking") - 1) foreach { siteId =>
        val clickCount = VolatilityCache.zscore("siteRanking", siteId).toLong

        // Redis上のクリック数をDBに反映
        siteDao.getById(siteId.toLong) match {
          case Some(x) => siteDao.update(x.copy(clickCount = x.clickCount + clickCount))
          case None =>
        }
      }

      VolatilityCache.zrevrange("articleRanking", 0, VolatilityCache.zcard("articleRanking") - 1) foreach { articleId =>
        val clickCount = VolatilityCache.zscore("articleRanking", articleId).toLong

        // Redis上のクリック数をDBに反映

        articleDao.getById(articleId.toLong) match {
          case Some(x) => articleDao.update(x.copy(clickCount = x.clickCount + clickCount))
          case None =>
        }
      }

      // キャッシュ情報をDBに追加したのでキャッシュは削除
      VolatilityCache.remove("siteRanking")
      VolatilityCache.remove("articleRanking")

    }
  }
}

object ClickLogService extends ClickLogService
