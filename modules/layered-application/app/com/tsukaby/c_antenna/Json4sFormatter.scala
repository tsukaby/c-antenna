package com.tsukaby.c_antenna

import com.github.tototoshi.play2.json4s.native.Json4s
import org.json4s.JsonAST.JValue
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Extraction}

/**
 * Json4sのフォーマッタ
 */
trait Json4sFormatter extends Json4s {
  implicit val formatter = DefaultFormats ++ JodaTimeSerializers.all

  def decompose(obj: Any): JValue = Extraction.decompose(obj)

  def extractOpt[T](value: JValue)(implicit mf: Manifest[T]): Option[T] = {
    value.extractOpt[T](formatter, mf)
  }

  def extract[T](value: JValue)(implicit mf: Manifest[T]): T = {
    value.extract[T](formatter, mf)
  }
}
