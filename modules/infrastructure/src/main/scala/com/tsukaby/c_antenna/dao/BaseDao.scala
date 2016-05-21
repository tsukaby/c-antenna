package com.tsukaby.c_antenna.dao

import org.slf4j.LoggerFactory

trait BaseDao {
  val Logger = LoggerFactory.getLogger(classOf[BaseDao])
}
