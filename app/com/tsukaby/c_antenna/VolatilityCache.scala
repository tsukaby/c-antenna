package com.tsukaby.c_antenna

import redis.clients.jedis.Jedis

/**
 * キャッシュ期間が短く再起動で消去するキャッシュ
 */
object VolatilityCache extends Redis {
  // 起動のためにフラッシュするDB(0番)
  override val jedis: Jedis = {
    val tmp = new Jedis("localhost")
    tmp.select(0)
    tmp
  }

}
