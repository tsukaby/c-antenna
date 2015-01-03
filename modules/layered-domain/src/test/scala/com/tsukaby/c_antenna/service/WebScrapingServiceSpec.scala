package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec

class WebScrapingServiceSpec extends BaseServiceSpec {
  val TargetClass = WebScrapingService

  s"$TargetClass#getImage" should {
    "サムネ画像が取得できること" in {
      val res = TargetClass.getImage("http://tsukaby.com/tech_blog")

      res.size must be greaterThan 0
    }
  }
}
