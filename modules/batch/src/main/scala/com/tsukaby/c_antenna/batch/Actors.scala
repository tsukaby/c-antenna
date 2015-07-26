package com.tsukaby.c_antenna.batch

import akka.actor.{Actor, ReceiveTimeout}
import com.tsukaby.c_antenna.actor.BaseActor
import com.tsukaby.c_antenna.dao.SiteDao
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.service._
import com.tsukaby.c_antenna.util.TimeUtil
import scalikejdbc._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * サイトのRSSクロールを行います。
 */
case class RssCrawlActor() extends BaseActor {

  context.setReceiveTimeout(5 minute)

  def receive: Actor.Receive = {
    case ReceiveTimeout =>
      log.info("timeout")
      context.stop(self)
    case all: RssCrawlActor.Protocol.CrawlAll =>
      SiteDao.getAll.foreach { x =>
        self ! x
      }
    case site: SiteMapper =>
      val f = SiteService.crawl(site)
      val wrapper = RssCrawlActor.Protocol.FutureAwait(f, site)
      self ! wrapper
    case wrapper: RssCrawlActor.Protocol.FutureAwait[_] =>
      if (!wrapper.f.isCompleted) {
        context.system.scheduler.scheduleOnce(10 seconds, self, wrapper)
      } else {
        log.info(s"Crawl completed! site = ${wrapper.site.name}")
      }
  }

}

object RssCrawlActor {
  object Protocol {
    case class FutureAwait[T](f: Future[T], site: SiteMapper)
    case class CrawlAll()
  }
}

case class SiteThumbnailActor() extends BaseActor {
  def receive: Actor.Receive = {
    case all: SiteThumbnailActor.Protocol.RefreshAll =>
      val result = TimeUtil.time(SiteService.refreshSiteThumbnail())
      log.info(s"サイトのサムネイルを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

object SiteThumbnailActor {
  object Protocol {
    case class RefreshAll()
  }
}

case class HatebuActor() extends BaseActor {
  def receive: Actor.Receive = {
    case all: HatebuActor.Protocol.RefreshAllSite =>
      val result = TimeUtil.time(SiteService.refreshSiteRank())
      log.info(s"サイトのランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
    case all: HatebuActor.Protocol.RefreshAllArticle =>
      val result = TimeUtil.time(ArticleService.refreshArticleRank())
      log.info(s"記事のランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
    case x: HatebuActor.Protocol.RefreshRecentArticle =>
      val result = TimeUtil.time(ArticleService.refreshRecentArticleRank())
      log.info(s"最近の記事のランキングを最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

object HatebuActor {
  object Protocol{
    case class RefreshAllSite()
    case class RefreshAllArticle()
    case class RefreshRecentArticle()
  }
}

case class RankingActor() extends BaseActor {
  def receive: Actor.Receive = {
    case all: RankingActor.Protocol.RefreshAll =>
      val result = DB localTx { session =>
        TimeUtil.time(ClickLogService.refreshRanking())
      }
      log.info(s"ランキングをDBに反映しました。 (${result._2.toSeconds} sec)")
  }
}

object RankingActor {
  object Protocol {
    case class RefreshAll()
  }
}

case class SiteNameActor() extends BaseActor {
  def receive: Actor.Receive = {
    case all: SiteNameActor.Protocol.RefreshAll =>
      val result = TimeUtil.time {
        val futures = SiteDao.getAll.map { x =>
          SiteService.refreshSiteName(x)
        }

        val f = Future.sequence(futures)
        Await.ready(f, Duration.Inf)
      }

      log.info(s"サイト名を最新状態にしました。 (${result._2.toSeconds} sec)")
  }
}

object SiteNameActor {
  object Protocol {
    case class RefreshAll()
  }
}
