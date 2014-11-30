package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseSpecification
import play.api.test.WithApplication

class WebScrapingServiceSpec extends BaseSpecification {

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
