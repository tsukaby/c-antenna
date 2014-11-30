package com.tsukaby.c_antenna.service

class WebScrapingServiceSpec extends BaseServiceSpecification {

  val TargetClass = WebScrapingService

  s"$TargetClass#getRss" should {

    "RSSが取得できること" in {
      val rss = TargetClass.getRss("http://hamusoku.com/index.rdf")

      rss must beSome
    }

    "存在しないURLに対してリクエストした場合、RSSが取得できないこと" in {
      val rss = TargetClass.getRss("http://example.com")

      rss must beNone
    }
  }

}
