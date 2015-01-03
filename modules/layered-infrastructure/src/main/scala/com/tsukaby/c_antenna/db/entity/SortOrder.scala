package com.tsukaby.c_antenna.db.entity

/**
 * ソート順を表現します。
 */
sealed trait SortOrder {
  val typeId: Int
}

object SortOrder {

  /**
   * 昇順
   */
  object Asc extends SortOrder {
    override val typeId: Int = 1
  }

  /**
   * 降順
   */
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
