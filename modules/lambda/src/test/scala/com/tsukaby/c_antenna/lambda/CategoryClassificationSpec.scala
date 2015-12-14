package com.tsukaby.c_antenna.lambda

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.specs2.mutable.Specification

class CategoryClassificationSpec extends Specification {

  "CategoryClassification" >> {
    "#classify" >> {
      "when gave words" >> {
        "it returns a category" in {
          val target = new CategoryClassification()
          val request =
            """
              |{
              |  "words": ["SONY"]
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.classify(in, out)

          val expected = """{"category":"アニメ・ゲーム"}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }
    }
  }
}
