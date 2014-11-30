package com.tsukaby.c_antenna.util

import com.tsukaby.c_antenna.entity.{ClickLog, SimpleSearchCondition}

import scalaz.Scalaz._

/**
 * テスト用の便利ツール
 */
object TestUtil {

  def getBaseCondition: SimpleSearchCondition = {
    SimpleSearchCondition(1.some, 10.some, none, none, none)
  }

  def getBaseClickLog: ClickLog = {
    ClickLog(1L.some, 1L.some)
  }
}
