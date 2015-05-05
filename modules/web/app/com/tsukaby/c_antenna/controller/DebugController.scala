package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.dao.SiteDao
import com.tsukaby.c_antenna.service.{ClickLogService, SiteService}
import com.tsukaby.c_antenna.util.TimeUtil
import play.api.Logger
import play.api.mvc.Action

/**
 * デバッグ用のコントローラ
 */
trait DebugController extends BaseController {

  def runRssCrawl = Action {
    val result = TimeUtil.time({
      val sites = SiteDao.getAll
      sites.par foreach { site =>
        SiteService.crawl(site)
      }
    })

    Logger.info(s"クロールに成功しました！ (${result._2.toSeconds} sec)")
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
