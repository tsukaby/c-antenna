package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.ClickLog
import com.tsukaby.c_antenna.service.ClickLogService
import play.api.mvc.Action
import spray.json._

/**
 * クリックログを保存して記事などのランキングを作成するコントローラです。
 */
trait ClickLogController extends BaseController {

  def clickLog = Action { request =>

    request.body.asText match {
      case None => BadRequest("Invalidated.")
      case Some(x) =>
        val clickLog = x.parseJson.convertTo[ClickLog]
        ClickLogService.storeClickLog(clickLog)
        Ok("")
    }
  }
}

object ClickLogController extends ClickLogController
