package com.tsukaby.c_antenna

import akka.actor.{ActorSystem, Props}
import com.tsukaby.c_antenna.batch.RssCrawlActor
import scalikejdbc.config.DBs

import scala.language.postfixOps
import scala.concurrent.Await

/**
 * Created by tsukaby on 2014/12/13.
 */
object Main {
  def main(args: Array[String]): Unit = {
    println("start")

    //DBs.setup()

    import scalikejdbc._
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = true,
      singleLineMode = true,
      logLevel = 'DEBUG,
      warningEnabled = true,
      warningThresholdMillis = 1000L,
      warningLogLevel = 'WARN
    )

    Class.forName("com.mysql.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://localhost:3306/C_ANTENNA?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8", "root", "")

    val system = ActorSystem("c-antenna")
    val actor = system.actorOf(Props[RssCrawlActor])

    import akka.pattern.ask
    import akka.util.Timeout
    import scala.concurrent.duration._

    implicit val timeout = Timeout(60 seconds)

    val f = actor ? "go"
    val res = Await.result(f, timeout.duration).asInstanceOf[String]


    DBs.close()

  }
}
