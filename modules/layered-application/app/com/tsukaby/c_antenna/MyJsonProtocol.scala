package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.entity._
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import spray.json._

/**
 * Json変換
 */
trait MyJsonProtocol extends DefaultJsonProtocol {

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    private lazy val format = ISODateTimeFormat.dateTimeNoMillis()
    def write(datetime: DateTime): JsValue = JsString(format.print(datetime.withZone(DateTimeZone.UTC)))
    def read(json: JsValue): DateTime = json match {
      case JsString(x) => format.parseDateTime(x)
      case x           => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

  implicit object SortOrderFormat extends RootJsonFormat[SortOrder] {
    override def read(json: JsValue): SortOrder = json match {
      case JsNumber(value) => SortOrder.valueOf(value.intValue())
      case x => deserializationError("Expected SortOrder as 1 or 2, but got " + x)
    }
    override def write(obj: SortOrder): JsValue = JsNumber(obj.typeId)
  }


  implicit val ArticleFormat = jsonFormat9(Article)
  implicit val ClickLogFormat = jsonFormat2(ClickLog)
  implicit val SortFormat = jsonFormat2(Sort)
  implicit val SimpleSearchConditionFormat = jsonFormat5(SimpleSearchCondition)
  implicit val SiteFormat = jsonFormat5(Site)
  implicit val ArticlePageFormat = jsonFormat2(ArticlePage)
  implicit val SitePageFormat = jsonFormat2(SitePage)
}
