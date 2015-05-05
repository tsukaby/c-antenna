package com.tsukaby.c_antenna.cache

import org.specs2.mutable.Specification
import redis.clients.jedis.{JedisPoolConfig, Jedis, JedisPool}

class RedisSpec extends Specification {

  val TargetClass = new Redis {

    override val jedisPool: JedisPool = {
      new JedisPool(new JedisPoolConfig(), "localhost")
    }

    override def jedisFromPool: Jedis = {
      val jedis = jedisPool.getResource
      jedis
    }
  }

  s"$TargetClass#set, get" should {
    "値をRedisに保存・取得できること" in {
      TargetClass.set("key", "value", 300)
      val value = TargetClass.get("key")

      value must beSome("value")
    }
  }
}
