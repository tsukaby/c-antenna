package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.util.TestUtil._
import play.api.test.{FakeRequest, WithApplication}
import spray.json._

class ClickLogControllerSpec extends BaseControllerSpecification {

  val TargetClass = ClickLogController

  s"$TargetClass#clickLog" should {

    "正常なRequestBodyの場合200が返ること" in new WithApplication {
      val res = TargetClass.clickLog(FakeRequest().withTextBody(getBaseClickLog.toJson.compactPrint))

      status(res) must be equalTo OK
    }

    "異常なRequestBodyの場合400が返ること" in new WithApplication {
      val res = TargetClass.clickLog(FakeRequest())

      status(res) must be equalTo BAD_REQUEST
    }

  }
}
