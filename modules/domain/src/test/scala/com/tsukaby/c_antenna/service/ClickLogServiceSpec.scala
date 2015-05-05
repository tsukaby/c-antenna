package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec
import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.entity.TestUtil._

class ClickLogServiceSpec extends BaseServiceSpec {

  val TargetClass = ClickLogService

  // Redisが絡むので並列実行させない
  sequential

  s"$TargetClass#storeClickLog" should {

    "クリックログがRedisに保存されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must beFalse
      VolatilityCache.exists("articleRanking") must beFalse

      TargetClass.storeClickLog(getBaseClickLog)

      VolatilityCache.exists("siteRanking") must beTrue
      VolatilityCache.exists("articleRanking") must beTrue

      VolatilityCache.zscore("siteRanking", "1") must be equalTo 1.0
      VolatilityCache.zscore("articleRanking", "1") must be equalTo 1.0

    }

    "クリックログに記事IDが入っていない場合、サイトランキングのみ更新されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must beFalse
      VolatilityCache.exists("articleRanking") must beFalse

      TargetClass.storeClickLog(getBaseClickLog.copy(articleId = None))

      VolatilityCache.exists("siteRanking") must beTrue
      VolatilityCache.exists("articleRanking") must beFalse //こちらは更新されない

      VolatilityCache.zscore("siteRanking", "1") must be equalTo 1.0
    }

    "クリックログにサイトIDが入っていない場合、記事ランキングのみ更新されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must beFalse
      VolatilityCache.exists("articleRanking") must beFalse

      TargetClass.storeClickLog(getBaseClickLog.copy(siteId = None))

      VolatilityCache.exists("siteRanking") must beFalse //こちらは更新されない
      VolatilityCache.exists("articleRanking") must beTrue

      VolatilityCache.zscore("articleRanking", "1") must be equalTo 1.0
    }

  }

  s"$TargetClass#refreshRanking" should {

    "クリックログがRedisからDBに移ること(Redis上から値が消えること)" in {

      val targetClass = new ClickLogService {
        override val siteDao = {
          val siteDao = mock[SiteDao]
          siteDao.getById(1L) returns None
          siteDao
        }

        override val articleDao = {
          val articleDao = mock[ArticleDao]
          articleDao.getById(1L) returns None
          articleDao
        }
      }

      // 予めクリック情報を貯めておく
      VolatilityCache.zincrby("siteRanking", 1, "1")
      VolatilityCache.zincrby("articleRanking", 1, "1")

      targetClass.refreshRanking()

      // 貯めておいたクリック情報が無くなっているはず
      VolatilityCache.exists("siteRanking") must beFalse
      VolatilityCache.exists("articleRanking") must beFalse
    }

  }
}
