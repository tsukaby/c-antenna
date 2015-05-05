package com.tsukaby.c_antenna.batch

import akka.actor.{ActorRef, Actor, ReceiveTimeout}
import com.tsukaby.c_antenna.actor.BaseActor
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.service._
import com.tsukaby.c_antenna.util.TimeUtil
import scalikejdbc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * サイトのRSSクロールを行います。
 */
class RssCrawlActor extends BaseActor {

  context.setReceiveTimeout(1 minute)

  def receive: Actor.Receive = {
    case ReceiveTimeout =>
      log.info("timeout")
      context.stop(self)
    case site: SiteMapper =>
      val f = Future {
        SiteService.crawl(site)
      }

      Await.ready(f, 60 seconds)
      context.stop(self)
  }

}

class SiteThumbnailActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteThumbnail())
      log.info(s"サイトのサムネイルを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class HatebuActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteRank())
      log.info(s"サイトのランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

class RankingActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = DB localTx { session =>
        TimeUtil.time(ClickLogService.refreshRanking())
      }
      log.info(s"ランキングをDBに反映しました。 (${result._2.toSeconds} sec)")
  }
}

class SiteNameActor extends BaseActor {
  def receive: Actor.Receive = {
    case e: String =>
      val result = TimeUtil.time(SiteService.refreshSiteName())
      log.info(s"サイト名を最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}
