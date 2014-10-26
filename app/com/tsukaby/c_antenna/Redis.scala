package com.tsukaby.c_antenna

import java.io._

import biz.source_code.base64Coder.Base64Coder
import play.api.Play
import redis.clients.jedis.Jedis
import play.api.Play.current

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
      Option(deserialize[T](Base64Coder.decode(obj)))
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

  class ClassLoaderObjectInputStream(stream:InputStream) extends ObjectInputStream(stream) {
    override protected def resolveClass(desc: ObjectStreamClass) = {
      Class.forName(desc.getName, false, Play.application.classloader)
    }
  }
}
