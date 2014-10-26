package com.tsukaby.c_antenna.batch

import akka.actor.{Props, Actor}
import com.tsukaby.c_antenna.service.SiteService
import play.api.Logger
import play.api.libs.concurrent.Akka
import scala.concurrent.duration._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

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

    Akka.system.scheduler.schedule(firstDelay, interval, Akka.system.actorOf(Props[CrawlActor]), "")
  }

}