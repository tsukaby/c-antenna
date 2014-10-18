package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}

object ImplicitConverter {

  implicit def dbSitesToSites(siteMapper: SiteMapper, articleMappers: Seq[ArticleMapper]): Site = {
    Site(siteMapper.id, siteMapper.name, siteMapper.url, null, dbArticlesToArticles(articleMappers))
  }

  implicit def dbArticleToArticle(articleMapper: ArticleMapper): Article = {
    val tags = articleMapper.tag match {
      case Some(x) => x.split(" ").toSeq
      case None => Seq()
    }
    Article(articleMapper.url, articleMapper.title, null, tags)
  }

  implicit def dbArticlesToArticles(articleMappers: Seq[ArticleMapper]): Seq[Article] = {
    articleMappers map (x => dbArticleToArticle(x))
  }

}
