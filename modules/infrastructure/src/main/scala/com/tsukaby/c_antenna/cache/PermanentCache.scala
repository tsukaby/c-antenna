package com.tsukaby.c_antenna.cache

import redis.clients.jedis.{Jedis, JedisPoolConfig, JedisPool}

/**
 * キャッシュ期間が長いオブジェクトのキャッシュ
 */
object PermanentCache extends Redis {
  // 基本的にFLASHしないDB(1番)
  override val jedisPool: JedisPool = {
    new JedisPool(new JedisPoolConfig(), "localhost")
  }

  override def jedisFromPool: Jedis = {
    val jedis = jedisPool.getResource
    jedis.select(1)
    jedis
  }
}
