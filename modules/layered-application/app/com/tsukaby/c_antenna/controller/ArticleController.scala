package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.db.entity.SimpleSearchCondition
import com.tsukaby.c_antenna.entity._
import com.tsukaby.c_antenna.service.ArticleService
import play.api.mvc.Action
import spray.json._

trait ArticleController extends BaseController {

  val articleService: ArticleService = ArticleService

  /**
   * 記事一覧をランキング上位順で返却します。
   * @return
   */
  def showAll(condition: SimpleSearchCondition) = Action {
    val page:ArticlePage = articleService.getByCondition(condition)

    Ok(page.toJson.compactPrint).as("application/json")
  }
}

object ArticleController extends ArticleController

