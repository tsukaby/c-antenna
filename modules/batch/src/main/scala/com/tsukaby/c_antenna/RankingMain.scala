package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.service.{ArticleService, SiteService}
import kamon.Kamon
import scalikejdbc.config.DBs

import scala.language.postfixOps

object RankingMain {
  def main(args: Array[String]): Unit = {
    Kamon.start()
    DBs.setupAll()

    try {
      SiteService.refreshSiteRank()
      ArticleService.refreshArticleRank()
      ArticleService.refreshRecentArticleRank()
    } finally {
      Kamon.shutdown()
    }
  }
}
