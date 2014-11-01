package com.tsukaby.c_antenna.entity

import java.net.URLEncoder

import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable

import scalaz.Scalaz._

/**
 * 単純なページング（検索）を行う条件です。
 */
case class SimpleSearchCondition(
                                  page: Option[Int], // ページ番号 1-origin
                                  count: Option[Int] // 取得する件数
                                  )

object SimpleSearchCondition {
  implicit val format = Json.format[SimpleSearchCondition]

  implicit val binder = new QueryStringBindable[SimpleSearchCondition] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SimpleSearchCondition]] = {
      val page = params.get("page") match {
        case Some(x) => x.headOption match {
          case Some(x) => x.toInt
          case None => 1
        }
        case None => 1
      }

      val count = params.get("count") match {
        case Some(x) => x.headOption match {
          case Some(x) => x.toInt
          case None => 10
        }
        case None => 10
      }

      Right(SimpleSearchCondition(page.some, count.some)).some
    }

    override def unbind(key: String, value: SimpleSearchCondition): String = {
      URLEncoder.encode(key, "utf-8") + "=" + "page=" + URLEncoder.encode(value.page.toString, "utf-8") + "&" + "count=" + URLEncoder.encode(value.page.toString, "utf-8")
    }
  }

}
