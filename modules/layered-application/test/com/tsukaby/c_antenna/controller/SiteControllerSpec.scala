package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.util.TestUtil._
import com.tsukaby.c_antenna.entity.{SimpleSearchCondition, SitePage}
import org.specs2.mutable.Specification
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.Result
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

import scala.concurrent.Future
import scalaz.Scalaz._

object SiteControllerSpec extends Specification with PlaySpecification {

  val TargetClass = SiteController

  s"$TargetClass#showAll" should {

    "サイト一覧が取得できること" in new WithApplication {
      val res = TargetClass.showAll(getBaseCondition)(FakeRequest())

      val page: SitePage = res

      status(res) must be equalTo OK
      contentType(res) must beSome("application/json")
      page.items.size must be greaterThan 0
    }

    "取得件数を1件にした場合、１件だけ取得できること" in new WithApplication {
      val res = TargetClass.showAll(getBaseCondition.copy(count = 1.some))(FakeRequest())

      val page: SitePage = res

      status(res) must be equalTo OK
      page.items.size must be equalTo 1
    }
  }

  implicit def responseToPage(res: Future[Result]): SitePage = {
    contentAsJson(res).validate[SitePage] match {
      case JsSuccess(value, path) => value
      case JsError(errors) => throw new IllegalStateException(errors.toString())
    }
  }
}
