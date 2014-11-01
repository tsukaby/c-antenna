package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import com.tsukaby.c_antenna.service.SiteService

object ImplicitConverter {

  implicit def dbSitesToSites(siteMapper: SiteMapper, articleMappers: Seq[ArticleMapper]): Site = {
    Site(siteMapper.id, siteMapper.name, siteMapper.url, "", dbArticlesToArticles(articleMappers))
  }

  implicit def dbSitesToSites(sites: Seq[SiteMapper]): Seq[Site] = {
    sites map (x => dbSitesToSites(x, ArticleDao.getLatelyBySiteId(x.id)))
  }

  implicit def dbSiteToOptionSite(siteMapper: SiteMapper): Option[Site] = {
    Option(Site(siteMapper.id, siteMapper.name, siteMapper.url, "", Seq()))
  }

  implicit def dbArticleToArticle(articleMapper: ArticleMapper): Article = {
    val tags = articleMapper.tag match {
      case Some(x) => x.split(" ").toSeq
      case None => Seq()
    }

    val siteName = SiteService.getById(articleMapper.siteId) match {
      case Some(x) => x.name
      case None => ""
    }
    Article(articleMapper.url, articleMapper.title, "", tags, siteName, articleMapper.createdAt)
  }

  implicit def dbArticlesToArticles(articleMappers: Seq[ArticleMapper]): Seq[Article] = {
    articleMappers map (x => dbArticleToArticle(x))
  }

}
