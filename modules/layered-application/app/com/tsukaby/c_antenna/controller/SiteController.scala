package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.SiteService
import play.api.mvc.Action
import spray.json._

trait SiteController extends BaseController {

  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = SiteService.getByCondition(condition)

    Ok(page.toJson.compactPrint).as("application/json")
  }
}

object SiteController extends SiteController
