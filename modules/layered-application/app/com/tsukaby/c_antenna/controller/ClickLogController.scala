package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.ClickLog
import com.tsukaby.c_antenna.service.ClickLogService
import play.api.mvc.Action
import spray.json._

/**
 * クリックログを保存して記事などのランキングを作成するコントローラです。
 */
trait ClickLogController extends BaseController {

  val clickLogService: ClickLogService = ClickLogService

  /**
   * ClickLogをデータストアに保存します。
   */
  def clickLog = Action { request =>

    request.body.asJson match {
      case None => BadRequest("Invalidated.")
      case Some(x) =>
        val clickLog = x.toString().parseJson.convertTo[ClickLog]
        clickLogService.storeClickLog(clickLog)
        Ok("")
    }
  }
}

object ClickLogController extends ClickLogController
