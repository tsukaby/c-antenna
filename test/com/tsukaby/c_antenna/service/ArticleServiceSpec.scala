package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.util.TestUtil._
import org.specs2.mutable.Specification
import play.api.test.{PlaySpecification, WithApplication}

import scalaz.Scalaz._

object ArticleServiceSpec extends Specification with PlaySpecification {

  val TargetClass = ArticleService

  s"$TargetClass#getLately" should {

    "記事一覧が取得できること" in new WithApplication {
      val page = TargetClass.getByCondition(getBaseCondition)

      page.items.size must be greaterThan 0
    }

    "取得件数を1件にした場合、１件だけ取得できること" in new WithApplication {
      val page = TargetClass.getByCondition(getBaseCondition.copy(count = 1.some))

      page.items.size must be equalTo 1
    }
  }
}
