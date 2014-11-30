package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SitePage
import com.tsukaby.c_antenna.util.TestUtil._
import play.api.mvc.Result
import play.api.test.{FakeRequest, WithApplication}
import spray.json._

import scala.concurrent.Future
import scalaz.Scalaz._

class SiteControllerSpec extends BaseControllerSpecification {

  val TargetClass = SiteController

  s"$TargetClass#showAll" should {

    "サイト一覧が取得できること" in new WithApplication {
      val res = TargetClass.showAll(getBaseCondition)(FakeRequest())

      val page: SitePage = res.convertTo[SitePage]

      status(res) must be equalTo OK
      contentType(res) must beSome("application/json")
      page.items.size must be greaterThan 0
    }

    "取得件数を1件にした場合、１件だけ取得できること" in new WithApplication {
      val res = TargetClass.showAll(getBaseCondition.copy(count = 1.some))(FakeRequest())

      val page: SitePage = res.convertTo[SitePage]

      status(res) must be equalTo OK
      page.items.size must be equalTo 1
    }
  }

  implicit def responseToPage[T](res: Future[Result]): JsValue = {
    contentAsString(res).parseJson
  }
}
