package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.ClickLog

object ClickLogService extends BaseService {

  def storeClickLog(clickLog: ClickLog) {
    clickLog.siteId match {
      case Some(x) =>
        // サイトのクリックカウントを１つ上げる
        Redis.zincrby("siteRanking", 1, x.toString)
      case None =>
    }

    clickLog.articleId match {
      case Some(x) =>
        // 記事のクリックカウントを１つ上げる
        Redis.zincrby("articleRanking", 1, x.toString)
      case None =>
    }
  }

  def refreshRanking(): Unit = {
    Redis.zrevrange("siteRanking", 0, Redis.zcard("siteRanking") - 1) foreach { siteId =>
      val clickCount = Redis.zscore("siteRanking", siteId).toLong

      // Redis上のクリック数をDBに反映
      SiteDao.getById(siteId.toLong) match {
        case Some(x) => SiteDao.update(x.copy(clickCount = x.clickCount + clickCount))
        case None =>
      }
    }

    Redis.zrevrange("articleRanking", 0, Redis.zcard("articleRanking") - 1) foreach { articleId =>
      val clickCount = Redis.zscore("articleRanking", articleId).toLong

      // Redis上のクリック数をDBに反映

      ArticleDao.getById(articleId.toLong) match {
        case Some(x) => ArticleDao.update(x.copy(clickCount = x.clickCount + clickCount))
        case None =>
      }
    }

  }

}
