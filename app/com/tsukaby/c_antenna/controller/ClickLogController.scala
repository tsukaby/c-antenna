package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.ClickLog
import com.tsukaby.c_antenna.service.ClickLogService
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, Controller}

/**
 * クリックログを保存して記事などのランキングを作成するコントローラです。
 */
trait ClickLogController extends Controller {

  def clickLog = Action { request =>

    val clickLog = request.body.asJson.get.validate[ClickLog] match {
      case JsSuccess(value, path) => value
      case JsError(errors) => throw new IllegalArgumentException(errors.toString())
    }

    ClickLogService.storeClickLog(clickLog)

    Ok("")
  }
}

object ClickLogController extends ClickLogController
