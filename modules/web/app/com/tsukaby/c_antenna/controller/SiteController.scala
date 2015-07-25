package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.SiteService
import play.api.mvc.Action

/**
 * Webサイトに関する処理を行います。
 */
trait SiteController extends BaseController {

  val siteService: SiteService = SiteService

  /**
   * 引数で指定した条件に従ってサイトを取得します。
   * @param condition サイトを取得する条件
   */
  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = siteService.getByCondition(condition)

    Ok(decompose(page)).as("application/json")
  }

}

object SiteController extends SiteController
