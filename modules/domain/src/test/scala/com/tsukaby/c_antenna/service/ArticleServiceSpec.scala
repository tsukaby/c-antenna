package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec

class ArticleServiceSpec extends BaseServiceSpec {
  val TargetClass = ArticleService

  /*

  implicit conversionしている部分がDaoに依存しているので上手くテストできない
  依存性を剥がしてmockでテストできるようにするか、mockは諦めてテストレコードを用意するかどちらか

  s"$TargetClass#getByCondition" should {
    "1件以上取得できること" in {
      val targetTest = new ArticleService {
        override val articleDao = {
          val articleDao = mock[ArticleDao]
          articleDao.getByCondition(getBaseCondition) returns Seq(getBaseArticleMapper)
          articleDao.countByCondition(getBaseCondition) returns 1L
          articleDao
        }
      }

      val res = targetTest.getByCondition(getBaseCondition)

      res.items.size must be greaterThan 0
    }
  }
  */
}
