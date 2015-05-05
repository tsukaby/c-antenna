package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec
import com.tsukaby.c_antenna.dao.SiteDao
import com.tsukaby.c_antenna.util.TestUtil._

import scalaz.Scalaz._

class SiteServiceSpec extends BaseServiceSpec {
  val TargetClass = SiteService

  /*

  implicit conversionしている部分がDaoに依存しているので上手くテストできない
  依存性を剥がしてmockでテストできるようにするか、mockは諦めてテストレコードを用意するかどちらか

  s"$TargetClass#getByCondition" should {
    "1件以上取得できること" in {
      val targetClass = new SiteService {
        override val siteSummaryDao = {
          val siteSummaryDao = mock[SiteSummaryDao]
          siteSummaryDao.getByCondition(getBaseCondition) returns Seq(getBaseSiteSummaryMapper)
          siteSummaryDao.countByCondition(getBaseCondition) returns 1L
          siteSummaryDao
        }
      }

      DBs.setupAll()

      val res = targetClass.getByCondition(getBaseCondition)

      res.items.size must be greaterThan 0
    }
  }
  */

  s"$TargetClass#getById" should {
    "1件取得できること" in {
      val targetClass = new SiteService {
        override val siteDao = mock[SiteDao]
        siteDao.getById(0L) returns getBaseSiteMapper.some
      }


      val res = targetClass.getById(0L)

      res must beSome
    }
  }
}
