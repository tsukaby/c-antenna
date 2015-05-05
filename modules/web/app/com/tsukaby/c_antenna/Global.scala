package com.tsukaby.c_antenna

import akka.actor.Props
import com.tsukaby.c_antenna.cache.VolatilityCache
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}
import us.theatr.akka.quartz.QuartzActor

import scala.language.postfixOps

/**
 * アプリケーションの設定
 */
object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    val quartzActor = Akka.system.actorOf(Props[QuartzActor])

    // キャッシュ削除
    VolatilityCache.flushDB()

  }
}
