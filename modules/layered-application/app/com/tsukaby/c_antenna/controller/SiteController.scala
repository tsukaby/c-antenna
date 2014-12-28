package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.{SiteService, ThumbnailService}
import play.api.mvc.Action
import spray.json._

trait SiteController extends BaseController {

  val siteService: SiteService = SiteService
  val thumbnailService: ThumbnailService = ThumbnailService

  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = siteService.getByCondition(condition)

    Ok(page.toJson.compactPrint).as("application/json")
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
