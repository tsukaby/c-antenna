package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import com.tsukaby.c_antenna.batch._
import scalikejdbc.config.DBs
import us.theatr.akka.quartz.{AddCronSchedule, QuartzActor}

import scala.language.postfixOps

object Main {
  val system = ActorSystem("mySystem")

  def main(args: Array[String]): Unit = {
    DBs.setupAll()

    startCron()
  }

  private def startCron() = {
    val quartzActor = system.actorOf(Props[QuartzActor])

    // クリックのランキングを保存するバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[RankingActor]), "0 */5 * * * ?", RankingActor.Protocol.RefreshAll)

    // サイト名を最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[RssCrawlActor]), "0 */3 * * * ?", RssCrawlActor.Protocol.CrawlAll())

    // サイト名を最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[SiteNameActor]), "0 0 3 * * ?", SiteNameActor.Protocol.RefreshAll)

    // サイトサムネを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[SiteThumbnailActor]), "0 0 4 * * ?", SiteThumbnailActor.Protocol.RefreshAll)

    // サイトランキングを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[HatebuActor]), "0 0 4 * * ?", HatebuActor.Protocol.RefreshAll)

  }

}