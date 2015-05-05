package com.tsukaby.c_antenna.service

import org.slf4j.LoggerFactory

/**
 * 全てのサービスの基底クラス。共通処理を定義。
 */
trait BaseService {
  val Logger = LoggerFactory.getLogger(classOf[BaseService])
}
