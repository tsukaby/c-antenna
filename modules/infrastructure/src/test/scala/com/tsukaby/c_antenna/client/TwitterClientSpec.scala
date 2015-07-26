package com.tsukaby.c_antenna.client

import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

class TwitterClientSpec extends Specification {
   val TargetClass = TwitterClient

   s"$TargetClass" >> {
     "#getHatebuCount" >> {
       "when target is not exists" >> {
         "it returns zero" in {
           val actual = Await.result(TargetClass.getTweetCount("http://example.com/tsukaby.html"), 30 seconds)
           val expected = TweetCount(url = "http://example.com/tsukaby.html", count = 0)

           actual must be equalTo expected
         }
       }

       "when target is exists" >> {
         "it returns greater than zero" in {
           val actual = Await.result(TargetClass.getTweetCount("https://www.google.co.jp"), 30 seconds)

           actual.count must beGreaterThan(0L)
         }
       }
     }
   }
 }
