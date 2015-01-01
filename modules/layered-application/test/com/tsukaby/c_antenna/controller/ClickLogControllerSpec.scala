package com.tsukaby.c_antenna.controller

import org.json4s.Extraction
import org.json4s.JsonAST.JNull
import play.api.test.{FakeRequest, WithApplication}
import com.github.tototoshi.play2.json4s.test.native.Helpers._
import com.tsukaby.c_antenna.entity.TestUtil._

class ClickLogControllerSpec extends BaseControllerSpecification {

  val TargetClass = ClickLogController

  s"$TargetClass#clickLog" should {

    "正常なRequestBodyの場合200が返ること" in new WithApplication {
      val res = TargetClass.clickLog(FakeRequest().withJson4sBody(Extraction.decompose(getBaseClickLog)))

      status(res) must be equalTo OK
    }

    "異常なRequestBodyの場合400が返ること" in new WithApplication {
      val res = TargetClass.clickLog(FakeRequest().withJson4sBody(JNull))

      status(res) must be equalTo BAD_REQUEST
    }

  }
}
