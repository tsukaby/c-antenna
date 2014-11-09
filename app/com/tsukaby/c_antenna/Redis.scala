package com.tsukaby.c_antenna

import java.io._

import biz.source_code.base64Coder.Base64Coder
import play.api.Play
import scala.collection.JavaConverters._
import redis.clients.jedis.{JedisPool, Jedis}
import play.api.Play.current

// TODO キャッシュキーを固定する仕組み
// TODO DB番号指定を足す　1はキャッシュ用途で起動のたびに消す 2はランキングなど永続化？用など分ける
// TODO onloadでキャッシュ削除する仕組み
trait Redis {
  val jedisPool: JedisPool

  def set(key: String, value: Any, expire: Int): Unit = {

    using[Unit] { jedis =>
      val obj = new String(Base64Coder.encode(serialize(value)))
      jedis.setex(key, expire, obj)
    }
  }

  def get[T](key: String): Option[T] = {

    using[Option[T]] { jedis =>
      val obj = jedis.get(key)
      if (obj == null) {
        None
      } else {
        Option(deserialize[T](Base64Coder.decode(obj)))
      }
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
    using { jedis =>
      jedis.del(key)
    }
  }

  def exists(key: String): Boolean = {
    using { jedis =>
      jedis.exists(key)
    }
  }

  def zincrby(key: String, score: Double, member: String): Double = {
    using { jedis =>
      jedis.zincrby(key, score, member)
    }
  }

  def zrevrange(key: String, start: Long, end: Long): Set[String] = {
    using { jedis =>
      jedis.zrevrange(key, start, end).asScala.toSet
    }
  }

  def zscore(key: String, element: String): Double = {
    using { jedis =>
      jedis.zscore(key, element)
    }
  }

  def zcard(key: String): Long = {
    using { jedis =>
      jedis.zcard(key)
    }
  }

  /**
   * Redis上のデータを削除します。
   */
  def flushAll(): Unit = {
    using { jedis =>
      jedis.flushAll()
    }
  }

  def flushDB(): Unit = {
    using { jedis =>
      jedis.flushDB()
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
      Class.forName(desc.getName, false, Play.application.classloader)
    }
  }

}
