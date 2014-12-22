package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import com.tsukaby.c_antenna.batch.RssCrawlActor
import scalikejdbc.config.{DBs, DBsWithEnv}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Main {
  def main(args: Array[String]): Unit = {
    Option(System.getProperty("config.resource")) match {
      case None =>
        DBs.setupAll()
      case Some(x) =>
        DBsWithEnv(x.replace(".conf", "")).setupAll()
    }

    crawl()

  }

  private def crawl() = {
    val system = ActorSystem("mySystem")
    val actor = system.actorOf(Props[RssCrawlActor])

    // なぜか非同期形式にしないと処理がActorが終了せず、そのうち実行できなくなる・・・
    implicit val timeout = Timeout(3 minutes)

    val f = actor ? "a"
    Await.result(f, timeout.duration)
    system.shutdown()
  }

}
