package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import com.tsukaby.c_antenna.actor.{Reaper, ShutdownReaper}
import com.tsukaby.c_antenna.batch.RssCrawlActor
import scalikejdbc.config.{DBs, DBsWithEnv}

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

    val reaper = system.actorOf(Props[ShutdownReaper])
    reaper ! Reaper.WatchMe(actor)

    actor ! "go"
  }

}
