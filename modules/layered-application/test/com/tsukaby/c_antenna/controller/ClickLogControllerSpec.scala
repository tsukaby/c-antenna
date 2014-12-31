package com.tsukaby.c_antenna.controller

import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication}

class ClickLogControllerSpec extends BaseControllerSpecification {

  val TargetClass = ClickLogController

  s"$TargetClass#clickLog" should {

    "正常なRequestBodyの場合200が返ること" in new WithApplication {
      val str =
        """
          |{
          |  "siteId": 1,
          |  "articleId": 1
          |}
          |
        """.stripMargin
      val res = TargetClass.clickLog(FakeRequest().withJsonBody(Json.parse(str)))

      status(res) must be equalTo OK
    }

    "異常なRequestBodyの場合400が返ること" in new WithApplication {
      val res = TargetClass.clickLog(FakeRequest())

      status(res) must be equalTo BAD_REQUEST
    }

  }
}
