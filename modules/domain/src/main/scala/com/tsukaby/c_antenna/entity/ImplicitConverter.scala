package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import org.apache.commons.codec.binary.Base64

import scala.language.implicitConversions

object ImplicitConverter {

  implicit def dbSitesToSites(siteMapper: SiteMapper, articleMappers: Seq[ArticleMapper]): Site = {
    Site(
      id = siteMapper.id,
      name = siteMapper.name,
      url = siteMapper.url,
      thumbnailUrl = siteMapper.thumbnailUrl,
      clickCount = siteMapper.clickCount,
      hatebuCount = siteMapper.hatebuCount,
      recentArticles = dbArticlesToArticles(articleMappers))
  }

  implicit def dbSitesToSites(sites: Seq[SiteMapper]): Seq[Site] = {
    sites map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  implicit def dbSiteToOptionSite(siteMapper: SiteMapper): Option[Site] = {
    Some(Site(
      id = siteMapper.id,
      name = siteMapper.name,
      url = siteMapper.url,
      thumbnailUrl = siteMapper.thumbnailUrl,
      clickCount = siteMapper.clickCount,
      hatebuCount = siteMapper.hatebuCount,
      recentArticles = Seq()))
  }

  implicit def dbArticleToArticle(articleMapper: ArticleMapper): Article = {
    val siteName = SiteDao.getById(articleMapper.siteId) match {
      case Some(x) => x.name
      case None => ""
    }

    Article(
      id = articleMapper.id,
      siteId = articleMapper.siteId,
      url = articleMapper.url,
      eyeCatchUrl = articleMapper.eyeCatchUrl,
      title = articleMapper.title,
      description = articleMapper.description,
      tags = articleMapper.tag.map(_.split(" ").toSeq).getOrElse(Nil),
      siteName = siteName,
      clickCount = articleMapper.clickCount,
      hatebuCount = articleMapper.hatebuCount,
      publishedAt = articleMapper.publishedAt
    )
  }

  implicit def dbArticlesToArticles(articleMappers: Seq[ArticleMapper]): Seq[Article] = {
    articleMappers map (x => dbArticleToArticle(x))
  }

  implicit def bytesToBase64String(src: Option[Array[Byte]]): Option[String] = {
    src match {
      case Some(x) => Some(Base64.encodeBase64String(x))
      case None => None
    }

  }

}
