package com.tsukaby.c_antenna.controller

import com.github.tototoshi.play2.json4s.test.native.Helpers._
import com.tsukaby.c_antenna.entity.SitePage
import com.tsukaby.c_antenna.entity.TestUtil._
import com.tsukaby.c_antenna.service.SiteService
import play.api.test.FakeRequest

class SiteControllerSpec extends BaseControllerSpecification {

  val TargetClass = SiteController

  s"$TargetClass#showAll" should {

    "サイト一覧が取得できること" in {

      val targetClass = new SiteController {
        override val siteService = {
          val siteService = mock[SiteService]
          siteService.getByCondition(getBaseCondition) returns SitePage(Seq(getBaseSite), 1)
          siteService
        }
      }

      val res = targetClass.showAll(getBaseCondition)(FakeRequest())

      val page = extract[SitePage](contentAsJson4s(res))

      status(res) must be equalTo OK
      contentType(res) must beSome("application/json")
      page.items.size must be greaterThan 0
    }
  }

}
