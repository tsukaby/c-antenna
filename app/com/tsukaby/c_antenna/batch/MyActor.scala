package com.tsukaby.c_antenna.batch

import akka.actor.Actor
import com.tsukaby.c_antenna.dao.SiteDao
import com.tsukaby.c_antenna.service._
import com.tsukaby.c_antenna.util.TimeUtil
import play.api.Logger

/**
 *
 */
class RssCrawlActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time({
        val sites = SiteDao.getAll
        sites.par foreach { site =>
          SiteService.crawl(site)
        }
      })

      Logger.info(s"クロールに成功しました！ (${result._2.toSeconds} sec)")
  }

}

class SiteNameActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteName())
      Logger.info(s"サイト名を最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class SiteThumbnailActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteThumbnail())
      Logger.info(s"サイトのサムネイルを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class HatebuActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteRank())
      Logger.info(s"サイトのランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class RankingActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(ClickLogService.refreshRanking())
      Logger.info(s"ランキングをDBに反映しました。 (${result._2.toSeconds} sec)")
  }
}
