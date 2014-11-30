package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.BaseSpecification
import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.util.TestUtil._
import play.api.test.{FakeRequest, WithApplication}
import spray.json._

class ClickLogControllerSpec extends BaseSpecification {

  val TargetClass = ClickLogController

  s"$TargetClass#lately" should {

    "ClickLogがRedisに保存されること" in new WithApplication {

      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      val res = TargetClass.clickLog(FakeRequest().withTextBody(getBaseClickLog.toJson.compactPrint))

      status(res) must be equalTo OK

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo true
    }

  }
}
