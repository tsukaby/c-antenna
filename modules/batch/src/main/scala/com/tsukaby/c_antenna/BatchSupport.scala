package com.tsukaby.c_antenna

import com.typesafe.config.ConfigFactory

import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.FiniteDuration

trait BatchSupport {
  private val config = ConfigFactory.load.getConfig("com.tsukaby.c-antenna")
  val timeout = config.as[FiniteDuration]("future-timeout")

  def run(args: Array[String]):Unit
}
