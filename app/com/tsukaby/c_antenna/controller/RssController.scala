package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.service.SiteService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object RssController extends Controller {
  def showAll = Action {

    val sites = SiteService.getAll

    Ok(Json.toJson(sites))
  }
}
