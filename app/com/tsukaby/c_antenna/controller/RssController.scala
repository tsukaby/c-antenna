package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.Site
import com.tsukaby.c_antenna.service.SiteService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object RssController extends Controller {
  def showAll = Action {

    val sites: Seq[Site] = SiteService.getAll

    Ok(Json.toJson(sites))
  }
}
