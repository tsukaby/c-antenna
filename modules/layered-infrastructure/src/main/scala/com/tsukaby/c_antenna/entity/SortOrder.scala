package com.tsukaby.c_antenna.entity

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
}
