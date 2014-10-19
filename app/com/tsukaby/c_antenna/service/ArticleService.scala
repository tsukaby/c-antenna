package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import com.tsukaby.c_antenna.entity.Article
import org.joda.time.DateTime
import scalikejdbc._

import com.tsukaby.c_antenna.entity.ImplicitConverter._

/**
 * 記事に関する処理を行うクラスです。
 */
object ArticleService {
  private val am = ArticleMapper.am

  def getAll: Seq[Article] = {
    // whereで絞って最新のもののみ取得 whereは検索高速化のため。
    ArticleMapper.findAllBy(sqls.gt(am.createdAt, new DateTime().minusDays(2)).orderBy(ArticleMapper.am.createdAt).desc.limit(50))
  }
}
