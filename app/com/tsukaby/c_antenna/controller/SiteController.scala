package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.SiteService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

trait SiteController extends Controller {
  def showAll(condition: SimpleSearchCondition) = Action { implicit request =>

    val page = SiteService.getByCondition(condition)

    Ok(Json.toJson(page)).as("application/json")
  }
}

object SiteController extends SiteController
