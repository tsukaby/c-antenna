package com.tsukaby.c_antenna.db.entity

import org.joda.time.DateTime

/**
 * 単純なページング（検索）を行う条件です。
 */
case class SimpleSearchCondition(
  page: Option[Int], // ページ番号 1-origin
  count: Option[Int], // 取得する件数
  maxId: Option[Long], // 取得するデータ群の最大ID 相対位置のズレ防止

  hasEyeCatch: Boolean,

  startDateTime: Option[DateTime],
  endDateTime: Option[DateTime],

  sort: Option[Sort]
)
