package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.dao.{ArticleDao, SiteDao}
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import org.apache.commons.codec.binary.Base64

import scala.language.implicitConversions

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

  implicit def dbSiteToOptionSite(siteMapper: SiteMapper): Option[Site] = {
    Some(Site(siteMapper.id, siteMapper.name, siteMapper.url, Seq()))
  }

  implicit def dbArticleToArticle(articleMapper: ArticleMapper): Article = {
    val siteName = SiteDao.getById(articleMapper.siteId) match {
      case Some(x) => x.name
      case None => ""
    }

    Article(
      articleMapper.id,
      articleMapper.siteId,
      articleMapper.url,
      articleMapper.eyeCatchUrl,
      articleMapper.title,
      articleMapper.tag.map(_.split(" ").toSeq).getOrElse(Nil),
      siteName,
      articleMapper.clickCount,
      articleMapper.publishedAt
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
