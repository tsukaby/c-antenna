package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.service.SiteService
import kamon.Kamon
import scalikejdbc._
import scalikejdbc.config.DBs

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object CrawlerMain {

  def main(args: Array[String]): Unit = {
    Kamon.start()
    DBs.setupAll()

    try {
      args.toList match {
        case "rss" :: "important" :: Nil =>
          crawlImportantSites()
        case "rss" :: "unimportant" :: Nil =>
          crawlUnimportantSites()
        case "site_thumbs" :: Nil =>
          SiteService.createSiteThumbnails()
        case "new_site" :: Nil =>
          val f = SiteService.crawlNewSite()
          Await.result(f, 5 minutes)
        case x =>
          throw new IllegalArgumentException(s"args = $x")
      }
    } finally {
      Kamon.shutdown()
    }
  }

  private def crawlImportantSites(): Unit = {
    val fList: List[Future[Unit]] = SiteMapper.findAllBy(sqls.eq(SiteMapper.sm.disabled, false).and.ge(SiteMapper.sm.hatebuCount, 10000)).map { x =>
      SiteService.crawl(x)
    }

    Await.result(Future.sequence(fList), 30 seconds)
  }

  private def crawlUnimportantSites(): Unit = {
    val fList: List[Future[Unit]] = SiteMapper.findAllBy(sqls.eq(SiteMapper.sm.disabled, false).and.lt(SiteMapper.sm.hatebuCount, 10000)).map { x =>
      SiteService.crawl(x)
    }

    Await.result(Future.sequence(fList), 30 seconds)
  }
}
