package com.tsukaby.c_antenna.lambda

import java.io.{InputStream, OutputStream}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

import scala.io.{Codec, Source}

case class ClassificationRequest(words: Seq[String])

case class ClassificationResponse(category: String)

class CategoryClassification {
  implicit val formats = DefaultFormats

  def classify(input: InputStream, output: OutputStream): Unit = {
    val req = parse(Source.fromInputStream(input)(Codec.UTF8).mkString).extract[ClassificationRequest]
    val response = ClassificationResponse(CategoryClassificationService.classify(req.words))
    val responseStr = Serialization.write(response)

    val result = responseStr.getBytes("UTF-8")
    output.write(result)
  }
}
