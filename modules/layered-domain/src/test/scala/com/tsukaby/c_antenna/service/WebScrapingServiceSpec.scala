package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec

class WebScrapingServiceSpec extends BaseServiceSpec {
  val TargetClass = WebScrapingService

  s"$TargetClass#getRss" should {
    "正常なRSS URLの場合、取得できること" in {
      val res = TargetClass.getRss("http://hamusoku.com/index.rdf")

      res must beSome
    }

    "異常なRSS URLの場合、取得できないこと" in {
      val res = TargetClass.getRss("http://example.com/feed-missing")

      res must beNone
    }

  }

  s"$TargetClass#getImage" should {
    "サムネ画像が取得できること" in {
      val res = TargetClass.getImage("http://tsukaby.com/tech_blog")

      res.size must be greaterThan 0
    }
  }
}
