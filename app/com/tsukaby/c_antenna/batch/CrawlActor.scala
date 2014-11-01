package com.tsukaby.c_antenna.batch

import akka.actor.Actor
import com.tsukaby.c_antenna.service.SiteService
import play.api.Logger

import scala.concurrent.duration._

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

object CrawlActor {

  def runCrawler = {
    // 一定時間毎にBatchActorにメッセージを送る
    val firstDelay = 10.seconds
    val interval = 10.seconds

    //Akka.system.scheduler.schedule(firstDelay, interval, Akka.system.actorOf(Props[CrawlActor]), "")
  }

}