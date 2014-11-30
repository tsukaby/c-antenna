package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseSpecification
import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.util.TestUtil._
import play.api.test.WithApplication

class ClickLogServiceSpec extends BaseSpecification {

  val TargetClass = ClickLogService

  s"$TargetClass#storeClickLog" should {

    "クリックログがRedisに保存されること" in new WithApplication {
      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog)

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo true

      VolatilityCache.zrevrange("siteRanking", 0, 0) must size(1)
      VolatilityCache.zrevrange("articleRanking", 0, 0) must size(1)

    }

    "クリックログに記事IDが入っていない場合、サイトランキングのみ更新されること" in new WithApplication {
      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog.copy(articleId = None))

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo false //こちらは更新されない

      VolatilityCache.zrevrange("siteRanking", 0, 0) must size(1)
      VolatilityCache.zrevrange("articleRanking", 0, 0) must size(0) //こちらは更新されない
    }

    "クリックログにサイトIDが入っていない場合、" in new WithApplication {
      // TODO どうするか決める
    }

  }

  s"$TargetClass#refreshRanking" should {

    "クリックログがRedisからDBに移ること" in new WithApplication {
      TargetClass.refreshRanking()
    }

  }
}
