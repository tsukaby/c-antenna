package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.VolatilityCache
import com.tsukaby.c_antenna.util.TestUtil._
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

object ClickLogControllerSpec extends Specification with PlaySpecification {

  val TargetClass = ClickLogController

  s"$TargetClass#lately" should {

    "ClickLogがRedisに保存されること" in new WithApplication {

      VolatilityCache.exists("siteRanking") must be equalTo false
      VolatilityCache.exists("articleRanking") must be equalTo false

      val res = TargetClass.clickLog(FakeRequest().withJsonBody(Json.toJson(getBaseClickLog)))

      status(res) must be equalTo OK

      VolatilityCache.exists("siteRanking") must be equalTo true
      VolatilityCache.exists("articleRanking") must be equalTo true
    }

  }
}
