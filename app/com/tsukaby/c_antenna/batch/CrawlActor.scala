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
      Logger.info("実行に成功しました！")
  }

}

object CrawlActor {

  def runCrawler = {
    // 1分毎にBatchActorにメッセージ"はい"を送る
    Akka.system.scheduler.schedule(10 seconds, 1 minutes, Akka.system.actorOf(Props[CrawlActor]), "")
  }

}