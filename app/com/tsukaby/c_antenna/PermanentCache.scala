package com.tsukaby.c_antenna

import redis.clients.jedis.Jedis

/**
 * キャッシュ期間が長いオブジェクトのキャッシュ
 */
object PermanentCache extends Redis {
  // 基本的にFLASHしないDB(1番)
  override val jedis: Jedis = {
    val tmp = new Jedis("localhost")
    tmp.select(1)
    tmp
  }
}
