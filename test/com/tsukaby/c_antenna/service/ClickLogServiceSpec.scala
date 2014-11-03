package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.util.TestUtil._
import org.specs2.mutable.Specification
import play.api.test.{PlaySpecification, WithApplication}

object ClickLogServiceSpec extends Specification with PlaySpecification {

  val TargetClass = ClickLogService

  s"$TargetClass#storeClickLog" should {

    "クリックログがRedisに保存されること" in new WithApplication {
      Redis.exists("siteRanking") must be equalTo false
      Redis.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog)

      Redis.exists("siteRanking") must be equalTo true
      Redis.exists("articleRanking") must be equalTo true

      Redis.zrevrange("siteRanking", 0, 0) must size(1)
      Redis.zrevrange("articleRanking", 0, 0) must size(1)

    }

    "クリックログに記事IDが入っていない場合、サイトランキングのみ更新されること" in new WithApplication {
      Redis.exists("siteRanking") must be equalTo false
      Redis.exists("articleRanking") must be equalTo false

      TargetClass.storeClickLog(getBaseClickLog.copy(articleId = None))

      Redis.exists("siteRanking") must be equalTo true
      Redis.exists("articleRanking") must be equalTo false //こちらは更新されない

      Redis.zrevrange("siteRanking", 0, 0) must size(1)
      Redis.zrevrange("articleRanking", 0, 0) must size(0) //こちらは更新されない
    }

    "クリックログにサイトIDが入っていない場合、" in new WithApplication {
      // TODO どうするか決める
    }

  }

  s"$TargetClass#refreshRanking" should {

    "クリックログがRedisからDBに移ること" in new WithApplication {
      TargetClass.refreshRanking()
    }

  }
}
