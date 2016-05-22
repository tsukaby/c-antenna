package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.service.SiteService
import kamon.Kamon
import scalikejdbc._
import scalikejdbc.config.DBs

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object CrawlerMain extends BatchSupport {

  override def run(args: Array[String]):Unit = {
    args.toList match {
      case "rss" :: "important" :: Nil =>
        crawlImportantSites()
      case "rss" :: "unimportant" :: Nil =>
        crawlUnimportantSites()
      case "site_thumbs" :: Nil =>
        SiteService.createSiteThumbnails()
      case "new_site" :: Nil =>
        val f = SiteService.crawlNewSite()
        Await.result(f, timeout)
      case x =>
        throw new IllegalArgumentException(s"args = $x")
    }
  }

  def main(args: Array[String]): Unit = {
    Kamon.start()
    DBs.setupAll()

    try {
      run(args)
    } finally {
      Kamon.shutdown()
    }
  }

  private def crawlImportantSites(): Unit = {
    val fList: List[Future[Unit]] = SiteMapper.findAllBy(sqls.eq(SiteMapper.sm.disabled, false).and.ge(SiteMapper.sm.hatebuCount, 10000)).map { x =>
      SiteService.crawl(x)
    }

    Await.result(Future.sequence(fList), timeout)
  }

  private def crawlUnimportantSites(): Unit = {
    val fList: List[Future[Unit]] = SiteMapper.findAllBy(sqls.eq(SiteMapper.sm.disabled, false).and.lt(SiteMapper.sm.hatebuCount, 10000)).map { x =>
      SiteService.crawl(x)
    }

    Await.result(Future.sequence(fList), timeout)
  }
}
