package com.tsukaby.c_antenna.lambda

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.specs2.mutable.Specification

class CategoryClassificationSpec extends Specification {

  "CategoryClassification" >> {
    "#classify" >> {
      "when gave words" >> {
        "it returns a cateogyr" in {
          val target = new CategoryClassification()
          val request =
            """
              |{
              |  "words": ["git", "github", "scala"]
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.analyze(in, out)

          val expected = """{"category":"technology"}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }
    }
  }
}
