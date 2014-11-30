package com.tsukaby.c_antenna.controller

import java.net.URLEncoder

import com.tsukaby.c_antenna.entity.{Sort, SortOrder, Site, SimpleSearchCondition}
import com.tsukaby.c_antenna.service.SiteService
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json._
import play.api.mvc.{QueryStringBindable, Action, Controller}

import scalaz.Scalaz._

trait SiteController extends Controller {

  implicit val format = new Format[SortOrder] {
    override def reads(json: JsValue): JsResult[SortOrder] = JsSuccess(SortOrder.valueOf(json.as[Int]))

    override def writes(o: SortOrder): JsValue = JsNumber(o.typeId)
  }

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

  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = SiteService.getByCondition(condition)

    implicit val format = Json.format[Site]

    Ok(Json.toJson(page)).as("application/json")
  }
}

object SiteController extends SiteController
