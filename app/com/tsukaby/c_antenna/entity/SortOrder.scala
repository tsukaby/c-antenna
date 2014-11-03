package com.tsukaby.c_antenna.entity

import play.api.libs.json._

sealed trait SortOrder {
  val typeId: Int
}

object SortOrder {

  object Asc extends SortOrder {
    override val typeId: Int = 1
  }

  object Desc extends SortOrder {
    override val typeId: Int = 2
  }

  def valueOf(value: Int): SortOrder = {
    value match {
      case Asc.typeId => Asc
      case Desc.typeId => Desc
      case _ => throw new IllegalArgumentException()
    }
  }

  implicit val format = new Format[SortOrder] {
    override def reads(json: JsValue): JsResult[SortOrder] = JsSuccess(SortOrder.valueOf(json.as[Int]))

    override def writes(o: SortOrder): JsValue = JsNumber(o.typeId)
  }
}
