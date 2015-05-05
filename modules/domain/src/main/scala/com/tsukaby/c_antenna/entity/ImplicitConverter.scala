package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper, SiteSummaryMapper}
import org.apache.commons.codec.binary.Base64

import scala.language.implicitConversions
import scalaz.Scalaz._

object ImplicitConverter {

  implicit def dbSitesToSites(siteMapper: SiteMapper, articleMappers: Seq[ArticleMapper]): Site = {
    Site(
      siteMapper.id,
      siteMapper.name,
      siteMapper.url,
      dbArticlesToArticles(articleMappers))
  }

  implicit def dbSitesToSites(sites: Seq[SiteMapper]): Seq[Site] = {
    sites map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  implicit def dbSiteMappersToSites(siteSummaryMapper: SiteSummaryMapper, articleMappers: Seq[ArticleMapper]): Site = {
    Site(
      siteSummaryMapper.id,
      siteSummaryMapper.name,
      siteSummaryMapper.url,
      dbArticlesToArticles(articleMappers))
  }

  implicit def dbSiteMappersToSites(sites: Seq[SiteSummaryMapper]): Seq[Site] = {
    sites map (x => dbSiteMappersToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  implicit def dbSiteToOptionSite(siteMapper: SiteMapper): Option[Site] = {
    Site(siteMapper.id, siteMapper.name, siteMapper.url, Seq()).some
  }

  implicit def dbArticleToArticle(articleMapper: ArticleMapper): Article = {
    val tags = articleMapper.tag match {
      case Some(x) => x.split(" ").toSeq
      case None => Seq()
    }

    val siteName = SiteDao.getById(articleMapper.siteId) match {
      case Some(x) => x.name
      case None => ""
    }

    Article(
      articleMapper.id,
      articleMapper.siteId,
      articleMapper.url,
      articleMapper.title,
      tags,
      siteName,
      articleMapper.createdAt,
      articleMapper.clickCount)
  }

  implicit def dbArticlesToArticles(articleMappers: Seq[ArticleMapper]): Seq[Article] = {
    articleMappers map (x => dbArticleToArticle(x))
  }

  implicit def bytesToBase64String(src: Option[Array[Byte]]): Option[String] = {
    src match {
      case Some(x) => Base64.encodeBase64String(x).some
      case None => none
    }

  }

}
