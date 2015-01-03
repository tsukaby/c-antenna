package com.tsukaby.c_antenna.batch

import akka.actor.Actor
import com.tsukaby.c_antenna.actor.BaseActor
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.service._
import com.tsukaby.c_antenna.util.TimeUtil
import play.api.Logger
import scalikejdbc.DB

/**
 * サイトのRSSクロールを行います。
 */
class RssCrawlActor extends BaseActor {
  def receive: Actor.Receive = {
    case site:SiteMapper =>
      SiteService.crawl(site.id)
  }

}

class SiteNameActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteName())
      Logger.info(s"サイト名を最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class SiteThumbnailActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteThumbnail())
      Logger.info(s"サイトのサムネイルを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class HatebuActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteRank())
      Logger.info(s"サイトのランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class RankingActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = DB localTx { session =>
        TimeUtil.time(ClickLogService.refreshRanking())
      }
      Logger.info(s"ランキングをDBに反映しました。 (${result._2.toSeconds} sec)")
  }
}
