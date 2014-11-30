package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.util.TestUtil._

class ClickLogServiceSpec extends BaseServiceSpecification {

  val TargetClass = ClickLogService

  s"$TargetClass#storeClickLog" should {

    "クリックログがRedisに保存されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog)

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo true

      VolatilityCache.zrevrange("siteRanking", 0, 0) must size(1)
      VolatilityCache.zrevrange("articleRanking", 0, 0) must size(1)

    }

    "クリックログに記事IDが入っていない場合、サイトランキングのみ更新されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog.copy(articleId = None))

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo false //こちらは更新されない

      VolatilityCache.zrevrange("siteRanking", 0, 0) must size(1)
      VolatilityCache.zrevrange("articleRanking", 0, 0) must size(0) //こちらは更新されない
    }

    "クリックログにサイトIDが入っていない場合、記事ランキングのみ更新されること" in {
      VolatilityCache.flushDB()

      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog.copy(siteId = None))

      VolatilityCache.exists("siteRanking") must be equalTo false //こちらは更新されない
      VolatilityCache.exists("articleRanking") must be equalTo true

      VolatilityCache.zrevrange("siteRanking", 0, 0) must size(0) //こちらは更新されない
      VolatilityCache.zrevrange("articleRanking", 0, 0) must size(1)
    }

  }

  s"$TargetClass#refreshRanking" should {

    "クリックログがRedisからDBに移ること" in {
      TargetClass.refreshRanking()

      success
    }

  }
}
