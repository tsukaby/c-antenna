package com.tsukaby.c_antenna.client

import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

class HatenaClientSpec extends Specification {
  val TargetClass = HatenaClient

  s"$TargetClass" >> {
    "#getHatebuCount" >> {
      "when target is not exists" >> {
        "it returns zero" in {
          val actual = Await.result(TargetClass.getHatebuCounts("http://example.com/aaaaaaaa" :: Nil), 30 seconds)
          val expected = HatebuCount(url = "http://example.com/aaaaaaaa", count = 0) :: Nil

          actual must be equalTo expected
        }
      }

      "when target is exists" >> {
        "it returns greater than zero" in {
          val actual = Await.result(TargetClass.getHatebuCounts("https://www.google.co.jp" :: Nil), 30 seconds)

          actual.head.count must beGreaterThan(0L)
        }
      }
    }
  }
}
