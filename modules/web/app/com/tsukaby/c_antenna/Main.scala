package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import com.tsukaby.c_antenna.actor.{Reaper, ShutdownReaper}
import com.tsukaby.c_antenna.batch.RssCrawlActor
import com.tsukaby.c_antenna.dao.SiteDao
import scalikejdbc.config.DBs

import scala.language.postfixOps

object Main {
  def main(args: Array[String]): Unit = {
    DBs.setupAll()
    crawl()
  }

  private def crawl() = {
    val system = ActorSystem("mySystem")

    val reaper = system.actorOf(Props[ShutdownReaper])

    SiteDao.getAll.foreach { x =>
      val actor = system.actorOf(Props[RssCrawlActor])
      reaper ! Reaper.WatchMe(actor)
      actor ! x
    }
  }

}
