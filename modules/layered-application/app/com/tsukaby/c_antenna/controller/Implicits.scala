package com.tsukaby.c_antenna.controller

import java.net.URLEncoder

import com.tsukaby.c_antenna.entity.{Sort, SortOrder, SimpleSearchCondition}
import org.joda.time.format.ISODateTimeFormat
import play.api.mvc.QueryStringBindable

import scalaz.Scalaz._

/**
 * routerのフェーズで利用するobject変換用定義
 */
object Implicits {
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
