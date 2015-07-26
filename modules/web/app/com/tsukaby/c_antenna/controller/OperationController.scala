package com.tsukaby.c_antenna.controller

import com.github.tototoshi.play2.json4s.native.Json4s
import com.tsukaby.c_antenna.dao.SiteDao
import com.tsukaby.c_antenna.service.{ArticleService, SiteService}
import play.api.mvc.Action


/**
 * HTTP経由で各種管理操作を行うコントローラです
 */
trait OperationController extends BaseController with Json4s {

  val siteDao: SiteDao = SiteDao
  val siteService: SiteService = SiteService
  val articleService: ArticleService = ArticleService

  def crawlRssAll = Action {
    siteDao.getAll foreach { site =>
      siteService.crawl(site)
    }
    Ok("")
  }

  def crawlRss(siteId: Long) = Action {
    siteDao.getById(siteId) match {
      case Some(site) =>
        siteService.crawl(site)
        Ok("")
      case None =>
        NotFound("")
    }
  }

  def refreshSiteThumbnailAll = Action {
    siteService.refreshSiteThumbnail
    Ok("")
  }

  def refreshArticleRankings = Action {
    articleService.refreshArticleRank
    Ok("")
  }

}

object OperationController extends OperationController
