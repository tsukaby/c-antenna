package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SitePage
import com.tsukaby.c_antenna.service.{ThumbnailService, SiteService}
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

      val targetClass = new SiteController {
        override val siteService = {
          val siteService = mock[SiteService]
          siteService.getByCondition(getBaseCondition) returns SitePage(Seq(getBaseSite), 1)
          siteService
        }
      }

      val res = targetClass.showAll(getBaseCondition)(FakeRequest())

      val page: SitePage = res.convertTo[SitePage]

      status(res) must be equalTo OK
      contentType(res) must beSome("application/json")
      page.items.size must be greaterThan 0
    }
  }

  s"$TargetClass#showThumbs" should {

    "サイトのサムネイルが取得できること" in new WithApplication {

      val targetClass = new SiteController {
        override val thumbnailService = {
          val thumbnailService = mock[ThumbnailService]
          thumbnailService.getSiteThumbnail(1L) returns Array[Byte]().some
          thumbnailService
        }
      }

      val res = targetClass.showThumbs(1L)(FakeRequest())

      status(res) must be equalTo OK
      contentType(res) must beSome("image/jpeg")
    }
  }

  implicit def responseToPage[T](res: Future[Result]): JsValue = {
    contentAsString(res).parseJson
  }
}
