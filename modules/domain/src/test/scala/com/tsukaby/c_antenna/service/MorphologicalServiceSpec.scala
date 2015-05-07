package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec

class MorphologicalServiceSpec extends BaseServiceSpec {

  val TargetClass = MorphologicalService

  s"$TargetClass#getTags" should {

    "文字列を形態素解析できること" in {
      val tags = TargetClass.getTags("すもももももももものうち")

      tags.size must be greaterThan 0
    }

    "文字列を形態素解析し、出現頻度のランキングを作成できること" in {
      val tags = TargetClass.getTags("東京品川東京品川東京")

      val top = tags(0)
      top._1 must be equalTo "東京"
      top._2 must be equalTo 3


      val second = tags(1)
      second._1 must be equalTo "品川"
      second._2 must be equalTo 2
    }
  }
}