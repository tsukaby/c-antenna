package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import com.tsukaby.c_antenna.batch.{RssCrawlActor, HatebuActor, RankingActor, SiteNameActor}
import kamon.Kamon
import scalikejdbc.config.DBs
import us.theatr.akka.quartz.{AddCronSchedule, QuartzActor}

import scala.language.postfixOps

object Main {

  def main(args: Array[String]): Unit = {
    Kamon.start()

    DBs.setupAll()

    val system = ActorSystem("c-antenna-batch")
    startCron(system)
  }

  private def startCron(system: ActorSystem) = {
    val quartzActor = system.actorOf(Props[QuartzActor])

    // クリックのランキングを保存するバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[RankingActor]), "0 */5 * * * ?", RankingActor.Protocol.RefreshAll())

    // RSSを収集するバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[RssCrawlActor]), "0 */3 * * * ?", RssCrawlActor.Protocol.CrawlAll())

    // サイト名を最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[SiteNameActor]), "0 0 3 * * ?", SiteNameActor.Protocol.RefreshAll())

    // サイトランキングを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[HatebuActor]), "0 0 4 * * ?", HatebuActor.Protocol.RefreshAllSite())

    // 全ての記事ランキングを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[HatebuActor]), "0 30 5 1 * ?", HatebuActor.Protocol.RefreshAllArticle())

    // 最近の記事ランキングを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(system.actorOf(Props[HatebuActor]), "0 10 * * * ?", HatebuActor.Protocol.RefreshRecentArticle())

  }

}
