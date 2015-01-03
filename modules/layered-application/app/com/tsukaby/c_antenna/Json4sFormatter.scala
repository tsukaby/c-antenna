package com.tsukaby.c_antenna

import java.text.SimpleDateFormat

import com.github.tototoshi.play2.json4s.native.Json4s
import org.json4s.JsonAST.JValue
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{Formats, DefaultFormats, Extraction}

/**
 * Json4sのフォーマッタ
 */
trait Json4sFormatter extends Json4s {
  implicit val formatter = FixedDefaultFormats ++ JodaTimeSerializers.all

  def decompose(obj: Any): JValue = Extraction.decompose(obj)

  def extractOpt[T](value: JValue)(implicit mf: Manifest[T]): Option[T] = {
    value.extractOpt[T](formatter, mf)
  }

  def extract[T](value: JValue)(implicit mf: Manifest[T]): T = {
    value.extract[T](formatter, mf)
  }
}

/**
 * json4s 3.2.10 のバグ対応。
 * 2015/1/3現在、play-json4sの都合上3.2.10を使う必要があります。
 * 3.2.10では日付型を変換するときTimeZoneを考慮しない仕様になっている為、これに対応する処理を挟みます。
 *
 *
 * 参考情報。
 * https://github.com/json4s/json4s/issues/60
 * https://github.com/json4s/json4s/pull/112
 */
trait FixedDefaultFormats extends DefaultFormats {

  override def lossless: Formats = new DefaultFormats {
    override def dateFormatter = {
      val f = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      f.setTimeZone(DefaultFormats.UTC)
      f
    }
  }

  override protected def dateFormatter: SimpleDateFormat = {
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    f.setTimeZone(DefaultFormats.UTC)
    f
  }
}

object FixedDefaultFormats extends FixedDefaultFormats