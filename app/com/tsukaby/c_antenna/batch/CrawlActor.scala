package com.tsukaby.c_antenna.batch

import akka.actor.{Props, Actor}
import com.tsukaby.c_antenna.service.SiteService
import play.api.Logger
import play.api.libs.concurrent.Akka

import scala.concurrent.duration._
import play.api.Play.current

/**
 *
 */
class CrawlActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      SiteService.crawl
      Logger.info(s"クロールに成功しました！ $e")
  }

}

class SampleActor extends Actor {
  def receive: Actor.Receive = {
    case e: String =>
      SiteService.refreshSiteName
      Logger.info(s"サイト名を最新状態にしました。")
  }
}

object CrawlActor {

  import us.theatr.akka.quartz._

  val quartzActor = Akka.system.actorOf(Props[QuartzActor])

  def runSiteNameMaintainer = {
    val destinationActorRef = Akka.system.actorOf(Props[SampleActor])
    // 毎日３時に実行
    quartzActor ! AddCronSchedule(destinationActorRef, "0 0 3 * * ?", "Refresh site name")
  }


  def runRssCrawler = {
    // 一定時間毎にBatchActorにメッセージを送る
    val firstDelay = 10.seconds
    val interval = 10.seconds

    //Akka.system.scheduler.schedule(firstDelay, interval, Akka.system.actorOf(Props[CrawlActor]), "")
  }

}