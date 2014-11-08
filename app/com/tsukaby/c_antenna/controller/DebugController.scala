package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.service.{ClickLogService, SiteService}
import play.api.mvc.{Action, Controller}

/**
 * デバッグ用のコントローラ
 */
trait DebugController extends Controller {
  def refreshSiteName = Action {
    SiteService.refreshSiteName()
    Ok("done")
  }

  def refreshSiteRanking = Action {
    ClickLogService.refreshRanking()
    Ok("done")
  }
}

object DebugController extends DebugController
