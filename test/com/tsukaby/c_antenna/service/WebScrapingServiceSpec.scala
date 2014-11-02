package com.tsukaby.c_antenna.service

import org.specs2.mutable.Specification
import play.api.test.{PlaySpecification, WithApplication}

object WebScrapingServiceSpec extends Specification with PlaySpecification {

  val TargetClass = WebScrapingService

  s"$TargetClass#getRss" should {

    "RSSが取得できること" in new WithApplication {
      val rss = TargetClass.getRss("http://hamusoku.com/index.rdf")

      rss must beSome
    }

    "存在しないURLに対してリクエストした場合、RSSが取得できないこと" in new WithApplication {
      val rss = TargetClass.getRss("http://example.com")

      rss must beNone
    }
  }

}
