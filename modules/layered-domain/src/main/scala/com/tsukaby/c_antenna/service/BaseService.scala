package com.tsukaby.c_antenna.service

import org.slf4j.LoggerFactory

trait BaseService {
  val Logger = LoggerFactory.getLogger(classOf[BaseService])
}
