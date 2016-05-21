package com.tsukaby.c_antenna.cache

import java.io._

import biz.source_code.base64Coder.Base64Coder
import com.typesafe.config.ConfigFactory
import redis.clients.jedis.{Jedis, JedisPool}

import scala.collection.JavaConverters._

/**
 * Redisキャッシュ
 */
trait Redis {
  val jedisPool: JedisPool
  val conf = ConfigFactory.load.getConfig("com.tsukaby.c-antenna.cache")
  val enabled: Boolean = conf.getBoolean("enabled")

  def set(key: String, value: Any, expire: Int): Unit = {
    if (enabled) {
      using[Unit] { jedis =>
        val obj = new String(Base64Coder.encode(serialize(value)))
        jedis.setex(key, expire, obj)
      }
    }
  }

  def get[T](key: String): Option[T] = {
    if (enabled) {
      using[Option[T]] { jedis =>
        val obj = jedis.get(key)
        if (obj == null) {
          None
        } else {
          Option(deserialize[T](Base64Coder.decode(obj)))
        }
      }
    } else {
      None
    }
  }

  def getOrElse[T](key: String, expire: Int = 0)(orElse: => T): T = {
    get[T](key).getOrElse {
      val value = orElse
      set(key, value, expire)
      value
    }
  }

  def remove(key: String) = {
    if (enabled) {
      using { jedis =>
        jedis.del(key)
      }
    } else {
      0L
    }
  }

  def exists(key: String): Boolean = {
    if (enabled) {
      using { jedis =>
        jedis.exists(key)
      }
    } else {
      false
    }
  }

  def zincrby(key: String, score: Double, member: String): Double = {
    if (enabled) {
      using { jedis =>
        jedis.zincrby(key, score, member)
      }
    } else {
      0
    }
  }

  def zrevrange(key: String, start: Long, end: Long): Set[String] = {
    if (enabled) {
      using { jedis =>
        jedis.zrevrange(key, start, end).asScala.toSet
      }
    } else {
      Set.empty
    }
  }

  def zscore(key: String, element: String): Double = {
    if (enabled) {
      using { jedis =>
        jedis.zscore(key, element)
      }
    } else {
      0
    }
  }

  def zcard(key: String): Long = {
    if (enabled) {
      using { jedis =>
        jedis.zcard(key)
      }
    } else {
      0L
    }
  }

  /**
   * Redis上のデータを削除します。
   */
  def flushAll(): Unit = {
    if (enabled) {
      using { jedis =>
        jedis.flushAll()
      }
    }
  }

  def flushDB(): Unit = {
    if (enabled) {
      using { jedis =>
        jedis.flushDB()
      }
    }
  }

  private def serialize(obj: Any): Array[Byte] = {
    var b: ByteArrayOutputStream = null
    var o: ObjectOutputStream = null
    try {
      b = new ByteArrayOutputStream()
      o = new ObjectOutputStream(b)

      o.writeObject(obj)
      o.flush()
      b.toByteArray
    } finally {
      if (b != null) {
        b.close()
      }
      if (o != null) {
        o.close()
      }
    }

  }

  private def deserialize[T](bytes: Array[Byte]): T = {
    val b = new ByteArrayInputStream(bytes)
    val o = new ClassLoaderObjectInputStream(b)
    try {
      o.readObject().asInstanceOf[T]
    } finally {
      o.close()
    }

  }

  private def using[T](f: Jedis => T) = {
    val jedis = jedisFromPool
    try {
      f(jedis)
    } finally {
      jedis.close()
    }
  }

  def jedisFromPool: Jedis

  class ClassLoaderObjectInputStream(stream: InputStream) extends ObjectInputStream(stream) {
    override protected def resolveClass(desc: ObjectStreamClass) = {
      Class.forName(desc.getName, false, this.getClass.getClassLoader)
    }
  }

}
