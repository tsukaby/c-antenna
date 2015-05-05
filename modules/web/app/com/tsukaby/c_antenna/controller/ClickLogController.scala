package com.tsukaby.c_antenna.controller

import com.github.tototoshi.play2.json4s.native.Json4s
import com.tsukaby.c_antenna.entity.ClickLog
import com.tsukaby.c_antenna.service.ClickLogService
import org.json4s._
import play.api.mvc.Action


/**
 * クリックログを保存して記事などのランキングを作成するコントローラです。
 */
trait ClickLogController extends BaseController with Json4s {

  val clickLogService: ClickLogService = ClickLogService

  /**
   * ClickLogをデータストアに保存します。
   */
  def clickLog = Action(json) { request =>
    request.body.extractOpt[ClickLog] match {
      case Some(x) =>
        clickLogService.storeClickLog(x)
        Ok("")
      case None =>
        BadRequest("Bad request body.")
    }
  }
}

object ClickLogController extends ClickLogController
