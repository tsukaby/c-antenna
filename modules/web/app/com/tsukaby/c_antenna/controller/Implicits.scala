package com.tsukaby.c_antenna.controller

import java.net.URLEncoder

import com.tsukaby.c_antenna.db.entity.{SimpleSearchCondition, Sort, SortOrder}
import org.joda.time.format.ISODateTimeFormat
import play.api.mvc.QueryStringBindable

/**
 * routerのフェーズで利用するobject変換用定義
 */
object Implicits {
  implicit val binder = new QueryStringBindable[SimpleSearchCondition] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SimpleSearchCondition]] = {
      val page = params.get("page") match {
        case Some(x) => x.headOption match {
          case Some(x) => Some(x.toInt)
          case None => Some(1)
        }
        case None => Some(1)
      }

      val count = params.get("count") match {
        case Some(x) => x.headOption match {
          case Some(x) => Some(x.toInt)
          case None => Some(10)
        }
        case None => Some(10)
      }

      val maxId = params.get("maxId").flatMap(_.headOption) match {
        case Some("") | None => None
        case Some(x) => Some(x.toLong)
      }

      val hasEyeCatch = params.get("hasEyeCatch") match {
        case Some(x) => x.headOption match {
          case Some("true") => true
          case _ => false
        }
        case None => false
      }

      val startDateTime = params.get("startDateTime") match {
        case Some(x) => x.headOption match {
          case Some(x) => Some(ISODateTimeFormat.dateTime().parseDateTime(x))
          case None => None
        }
        case None => None
      }

      val endDateTime = params.get("endDateTime") match {
        case Some(x) => x.headOption match {
          case Some(x) => Some(ISODateTimeFormat.dateTime().parseDateTime(x))
          case None => None
        }
        case None => None
      }

      val sortKey = params.get("sort[key]") match {
        case Some(x) => Some(x.head)
        case None => None
      }

      val sortOrder = params.get("sort[order]") match {
        case Some(x) => Some(SortOrder.valueOf(x.head.toInt))
        case None => None
      }

      val sort = if (sortKey.isDefined && sortOrder.isDefined) {
        Some(Sort(sortKey.get, sortOrder.get))
      } else {
        None
      }

      Some(Right(SimpleSearchCondition(
        page = page,
        count = count,
        maxId = maxId,
        hasEyeCatch = hasEyeCatch,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        sort = sort)))
    }

    override def unbind(key: String, value: SimpleSearchCondition): String = {
      URLEncoder.encode(key, "utf-8") + "=" + "page=" + URLEncoder.encode(value.page.toString, "utf-8") + "&" + "count=" + URLEncoder.encode(value.page.toString, "utf-8") + s"&eyeCatchOnly=${value.hasEyeCatch}"
    }
  }

}
