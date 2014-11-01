package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.ArticleService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object ArticleController extends Controller {

  /**
   * 最新の記事一覧を投稿日時の新しい順に返却します。
   * @return
   */
  def lately(condition: SimpleSearchCondition) = Action {
    val page = ArticleService.getLately(condition)

    Ok(Json.toJson(page)).as("application/json")
  }
}
