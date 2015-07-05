package com.tsukaby.c_antenna.controller

import com.github.tototoshi.play2.json4s.test.native.Helpers._
import com.tsukaby.c_antenna.entity.ArticlePage
import com.tsukaby.c_antenna.entity.TestUtil._
import com.tsukaby.c_antenna.service.ArticleService
import play.api.http.MimeTypes
import play.api.test.FakeRequest

class ArticleControllerSpec extends BaseControllerSpecification {

  val TargetClass = ArticleController

  s"$TargetClass#getByCondition" should {

    "記事一覧が取得できること" in {
      val targetClass = new ArticleController {
        override val articleService = {
          val articleService = mock[ArticleService]
          articleService.getByCondition(getBaseCondition) returns ArticlePage(Seq(getBaseArticle), 1)
          articleService
        }
      }

      val res = targetClass.showAll(getBaseCondition)(FakeRequest())
      val page = extract[ArticlePage](contentAsJson4s(res))

      status(res) must be equalTo OK
      contentType(res) must beSome(MimeTypes.JSON)
      page.items.size must be greaterThan 0
    }
  }

}
