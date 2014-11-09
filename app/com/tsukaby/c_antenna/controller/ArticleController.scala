package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.service.ArticleService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

trait ArticleController extends Controller {

  /**
   * 記事一覧をランキング上位順で返却します。
   * @return
   */
  def ranking(condition: SimpleSearchCondition) = Action {
    val page = ArticleService.getByCondition(condition)

    Ok(Json.toJson(page)).as("application/json")
  }
}

object ArticleController extends ArticleController
