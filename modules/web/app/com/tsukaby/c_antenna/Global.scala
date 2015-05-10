package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.cache.VolatilityCache
import play.api.{Application, GlobalSettings}

import scala.language.postfixOps

/**
 * アプリケーションの設定
 */
object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    // キャッシュ削除
    VolatilityCache.flushDB()

  }
}
