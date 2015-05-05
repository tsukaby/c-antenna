package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.{SiteService, ThumbnailService}
import play.api.mvc.Action

/**
 * Webサイトに関する処理を行います。
 */
trait SiteController extends BaseController {

  val siteService: SiteService = SiteService
  val thumbnailService: ThumbnailService = ThumbnailService

  /**
   * 引数で指定した条件に従ってサイトを取得します。
   * @param condition サイトを取得する条件
   */
  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = siteService.getByCondition(condition)

    Ok(decompose(page)).as("application/json")
  }

  /**
   * サイトのサムネイルを取得します。
   * @param id 取得するサムネイルのサイトID
   */
  def showThumbs(id: Long) = Action { implicit request =>
    val byteArray: Array[Byte] = thumbnailService.getSiteThumbnail(id) match {
      case Some(x) => x
      case None => Array[Byte]()
    }

    Ok(byteArray).as("image/jpeg")
  }
}

object SiteController extends SiteController
