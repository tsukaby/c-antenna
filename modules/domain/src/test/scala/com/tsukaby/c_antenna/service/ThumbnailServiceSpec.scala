package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.BaseServiceSpec
import com.tsukaby.c_antenna.dao.SiteDao

import com.tsukaby.c_antenna.util.TestUtil._

import scalaz.Scalaz._

class ThumbnailServiceSpec extends BaseServiceSpec {
  val TargetClass = ThumbnailService

  s"$TargetClass#getByCondition" should {
    "正しい引数の場合、バイナリが取得できること" in {
      val targetClass = new ThumbnailService {
        override val siteDao = {
          val siteDao = mock[SiteDao]
          siteDao.getById(1L) returns getBaseSiteMapper.some
          siteDao
        }
      }

      val res = targetClass.getSiteThumbnail(1L)

      res must beSome
    }

    "存在しないサイトのIDを指定した場合、結果が取得できないこと" in {
      val targetClass = new ThumbnailService {
        override val siteDao = {
          val siteDao = mock[SiteDao]
          siteDao.getById(999L) returns None
          siteDao
        }
      }

      val res = targetClass.getSiteThumbnail(999L)

      res must beNone
    }
  }
}
