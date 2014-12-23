package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.ArticlePage
import com.tsukaby.c_antenna.service.ArticleService
import com.tsukaby.c_antenna.util.TestUtil._
import play.api.http.MimeTypes
import play.api.mvc.Result
import play.api.test.{FakeRequest, WithApplication}
import spray.json._

import scala.concurrent.Future

class ArticleControllerSpec extends BaseControllerSpecification {

  val TargetClass = ArticleController

  s"$TargetClass#getByCondition" should {

    "記事一覧が取得できること" in new WithApplication {
      val targetClass = new ArticleController {
        override val articleService = {
          val articleService = mock[ArticleService]
          articleService.getByCondition(getBaseCondition) returns ArticlePage(Seq(getBaseArticle), 1)
          articleService
        }
      }

      val res = targetClass.showAll(getBaseCondition)(FakeRequest())

      val page = res.convertTo[ArticlePage]

      status(res) must be equalTo OK
      contentType(res) must beSome(MimeTypes.JSON)
      page.items.size must be greaterThan 0
    }
  }

  implicit def responseToPage[T](res: Future[Result]): JsValue = {
    contentAsString(res).parseJson
  }

}
