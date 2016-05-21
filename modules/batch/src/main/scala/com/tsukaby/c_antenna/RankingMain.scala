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
      args.toList match {
        case "site_rank" :: Nil =>
          SiteService.refreshSiteRank()
        case "article_rank" :: Nil =>
          ArticleService.refreshArticleRank()
        case "recent_article_rank" :: Nil =>
          ArticleService.refreshRecentArticleRank()
        case x =>
          throw new IllegalArgumentException(s"args = $x")
      }
    } finally {
      Kamon.shutdown()
    }
  }
}
