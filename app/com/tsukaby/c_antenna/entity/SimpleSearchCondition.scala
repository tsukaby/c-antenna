package com.tsukaby.c_antenna.entity

import java.net.URLEncoder

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable

import scalaz.Scalaz._

/**
 * 単純なページング（検索）を行う条件です。
 */
case class SimpleSearchCondition(
                                  page: Option[Int], // ページ番号 1-origin
                                  count: Option[Int], // 取得する件数

                                  startDateTime: Option[DateTime],
                                  endDateTime: Option[DateTime],

                                  sort: Option[Sort]
                                  )

object SimpleSearchCondition {
  implicit val format = Json.format[SimpleSearchCondition]

  implicit val binder = new QueryStringBindable[SimpleSearchCondition] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SimpleSearchCondition]] = {
      val page = params.get("page") match {
        case Some(x) => x.headOption match {
          case Some(x) => x.toInt.some
          case None => 1.some
        }
        case None => 1.some
      }

      val count = params.get("count") match {
        case Some(x) => x.headOption match {
          case Some(x) => x.toInt.some
          case None => 10.some
        }
        case None => 10.some
      }

      val startDateTime = params.get("startDateTime") match {
        case Some(x) => x.headOption match {
          case Some(x) => ISODateTimeFormat.dateTime().parseDateTime(x).some
          case None => none
        }
        case None => none
      }

      val endDateTime = params.get("endDateTime") match {
        case Some(x) => x.headOption match {
          case Some(x) => ISODateTimeFormat.dateTime().parseDateTime(x).some
          case None => none
        }
        case None => none
      }

      val sortKey = params.get("sort[key]") match {
        case Some(x) => x.head.some
        case None => none
      }

      val sortOrder = params.get("sort[order]") match {
        case Some(x) => SortOrder.valueOf(x.head.toInt).some
        case None => none
      }

      val sort = if (sortKey.isDefined && sortOrder.isDefined) {
        Sort(sortKey.get, sortOrder.get).some
      } else {
        none
      }

      Right(SimpleSearchCondition(page, count, startDateTime, endDateTime, sort)).some
    }

    override def unbind(key: String, value: SimpleSearchCondition): String = {
      URLEncoder.encode(key, "utf-8") + "=" + "page=" + URLEncoder.encode(value.page.toString, "utf-8") + "&" + "count=" + URLEncoder.encode(value.page.toString, "utf-8")
    }
  }

}
