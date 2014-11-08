package com.tsukaby.c_antenna.batch

import akka.actor.Actor
import com.tsukaby.c_antenna.service.{ClickLogService, SiteService}
import com.tsukaby.c_antenna.util.TimeUtil
import play.api.Logger

/**
 *
 */
class RssCrawlActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.crawl)
      Logger.info(s"クロールに成功しました！ (${result._2.toSeconds} ms)")
  }

}

class SampleActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteName())
      Logger.info(s"サイト名を最新状態にしました。 (${result._2.toSeconds}} ms)")
  }
}

class RankingActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(ClickLogService.refreshRanking())
      Logger.info(s"ランキングをDBに反映しました。 (${result._2.toSeconds}} ms)")
  }
}
