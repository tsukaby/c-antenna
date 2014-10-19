package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.db.mapper.{ArticleMapper, SiteMapper}
import com.tsukaby.c_antenna.entity.Site
import com.tsukaby.c_antenna.entity.ImplicitConverter._

import scalikejdbc._

object SiteService extends BaseService {

  /**
   * 全てのサイトの情報を取得します。
   * RSS記事情報は最新の5件のみ取得します。
   *
   * @return
   */
  def getAll: Seq[Site] = {
    val targets = SiteMapper.findAll()

    targets map (x => dbSitesToSites(x, ArticleMapper.findAllBy(sqls.eq(ArticleMapper.am.siteId, x.id).orderBy(ArticleMapper.am.createdAt).desc.limit(5))))
  }

  def getById(id: Long) : Option[Site] = {
    SiteMapper.find(id) match {
      case Some(x) => x
      case None => None
    }
  }
}
