package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.VolatilityCache
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.ClickLog

object ClickLogService extends BaseService {

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

  def refreshRanking(): Unit = {
    VolatilityCache.zrevrange("siteRanking", 0, VolatilityCache.zcard("siteRanking") - 1) foreach { siteId =>
      val clickCount = VolatilityCache.zscore("siteRanking", siteId).toLong

      // Redis上のクリック数をDBに反映
      SiteDao.getById(siteId.toLong) match {
        case Some(x) => SiteDao.update(x.copy(clickCount = x.clickCount + clickCount))
        case None =>
      }
    }

    VolatilityCache.zrevrange("articleRanking", 0, VolatilityCache.zcard("articleRanking") - 1) foreach { articleId =>
      val clickCount = VolatilityCache.zscore("articleRanking", articleId).toLong

      // Redis上のクリック数をDBに反映

      ArticleDao.getById(articleId.toLong) match {
        case Some(x) => ArticleDao.update(x.copy(clickCount = x.clickCount + clickCount))
        case None =>
      }
    }

  }

}
