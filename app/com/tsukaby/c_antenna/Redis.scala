package com.tsukaby.c_antenna

import java.io._

import biz.source_code.base64Coder.Base64Coder
import redis.clients.jedis.Jedis

object Redis {
  val jedis: Jedis = new Jedis("localhost")

  def set(key: String, value: Any, expire: Int): Unit = {
    val obj = new String(Base64Coder.encode(serialize(value)))
    jedis.setex(key, expire, obj)
  }

  def get[T](key: String): Option[T] = {
    val obj = jedis.get(key)
    if (obj == null) {
      None
    } else {
      Option(deserialize(Base64Coder.decode(obj)).asInstanceOf[T])
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
    jedis.del(key)
  }

  private def serialize(obj: Any): Array[Byte] = {
    val b: ByteArrayOutputStream = new ByteArrayOutputStream()
    val o: ObjectOutputStream = new ObjectOutputStream(b)
    o.writeObject(obj)
    b.toByteArray
  }

  private def deserialize(bytes: Array[Byte]): Any = {
    val b: ByteArrayInputStream = new ByteArrayInputStream(bytes)
    val o: ObjectInputStream = new ObjectInputStream(b)
    o.readObject()
  }
}
