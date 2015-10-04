package com.tsukaby.c_antenna.lambda

import java.io.{InputStream, OutputStream}

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

case class AnalyzeRequest(text: String)

case class AnalyzeResponse(tags: Seq[String])

class Morphological {
  implicit val formats = DefaultFormats

  def analyze(input: InputStream, output: OutputStream): Unit = {
    val textInfo = parse(input).extract[AnalyzeRequest]
    val response = AnalyzeResponse(MorphologicalService.getTags(textInfo.text).map(_._1))
    val responseStr = Serialization.write(response)

    val result = responseStr.getBytes("UTF-8")
    output.write(result)
  }
}
