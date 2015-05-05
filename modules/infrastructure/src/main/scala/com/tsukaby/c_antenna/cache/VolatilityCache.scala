package com.tsukaby.c_antenna.cache

import redis.clients.jedis.{Jedis, JedisPoolConfig, JedisPool}

/**
 * キャッシュ期間が短く再起動で消去するキャッシュ
 */
object VolatilityCache extends Redis {
  // 起動のためにフラッシュするDB(0番)
  override val jedisPool: JedisPool = {
    new JedisPool(new JedisPoolConfig(), "localhost")
  }

  override def jedisFromPool: Jedis = {
    val jedis = jedisPool.getResource
    jedis
  }


}
