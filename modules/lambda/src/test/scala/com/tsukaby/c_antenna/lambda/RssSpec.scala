package com.tsukaby.c_antenna.lambda

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.specs2.mutable.Specification

class RssSpec extends Specification {

  "Rss" >> {
    "#findRssUrl" >> {
      "when gave validated url" >> {
        "it returns a rssUrl" in {
          val target = new Rss()
          val request =
            """
              |{
              |  "pageUrl": "http://gigazine.net/"
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.findRssUrl(in, out)

          val expected = """{"rssUrl":"http://gigazine.net/news/rss_2.0/"}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }

      "when gave invalidated url (Page exists but Rss url doesn't exists)" >> {
        "it returns a null" in {
          val target = new Rss()
          val request =
            """
              |{
              |  "pageUrl": "http://www.tsukaby.com/"
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.findRssUrl(in, out)

          val expected = """{"rssUrl":null}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }

      "when gave invalidated url (302)" >> {
        "it returns a null" in {
          val target = new Rss()
          val request =
            """
              |{
              |  "pageUrl": "http://tsukaby.com/"
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.findRssUrl(in, out)

          val expected = """{"rssUrl":null}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }

      "when gave invalidated url (Page doesn't exists)" >> {
        "it returns a null" in {
          val target = new Rss()
          val request =
            """
              |{
              |  "pageUrl": "http://example.com"
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.findRssUrl(in, out)

          val expected = """{"rssUrl":null}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }
    }
  }
}
