package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.service.{ClickLogService, SiteService}
import play.api.mvc.{Action, Controller}

/**
 * デバッグ用のコントローラ
 */
trait DebugController extends Controller {

  def runRssCrawl = Action {
    SiteService.crawl
    Ok("done")
  }

  def refreshSiteName = Action {
    SiteService.refreshSiteName()
    Ok("done")
  }

  def refreshRanking = Action {
    ClickLogService.refreshRanking()
    Ok("done")
  }

  def refreshHatebuRanking = Action {
    SiteService.refreshSiteRank()
    Ok("done")
  }
}

object DebugController extends DebugController
