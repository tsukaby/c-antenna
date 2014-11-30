package com.tsukaby.c_antenna.entity

import org.joda.time.DateTime

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
