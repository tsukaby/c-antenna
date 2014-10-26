package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.service.SiteService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object SiteController extends Controller {
  def showAll = Action { implicit request =>

    val page = request.getQueryString("page") match {
      case Some(x) => x.toInt
      case None => 1
    }

    val count = request.getQueryString("count") match {
      case Some(x) => x.toInt
      case None => 3
    }

    val sites = SiteService.getWithPaging(page, count)

    Ok(Json.toJson(sites))
  }
}
