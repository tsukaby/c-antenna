package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import org.joda.time.DateTime
import scalikejdbc._

/**
 * 記事に関する操作を行います。
 */
object ArticleDao {

  private val am = ArticleMapper.am

  /**
   * 記事を作成します。
   *
   * @param id URL
   * @param siteId サイトID
   * @param title タイトル
   * @param tags タグ
   * @param createdAt 記事作成日時
   * @return 作成された記事
   */
  def create(id: String, siteId: Long, title: String, tags: Option[String], createdAt: DateTime): ArticleMapper = {
    val createdArticle = ArticleMapper.create(id, siteId, title, tags, createdAt)
    Redis.set(s"article:$id", Some(createdArticle), 300)

    createdArticle
  }

  /**
   * 記事を取得します。
   *
   * @param id 取得する記事のID
   */
  def getById(id: String): Option[ArticleMapper] = {
    Redis.getOrElse[Option[ArticleMapper]](s"article:$id", 300) {
      ArticleMapper.find(id)
    }
  }

  /**
   * 最新記事を取得します。
   *
   * @return 最新記事の一覧
   */
  def getLately: Seq[ArticleMapper] = {
    // whereで絞って最新のもののみ取得 whereは検索高速化のため。
    Redis.getOrElse[Seq[ArticleMapper]]("lately", 60) {
      ArticleMapper.findAllBy(sqls.gt(am.createdAt, new DateTime().minusDays(2)).orderBy(ArticleMapper.am.createdAt).desc.limit(50)).toSeq
    }
  }

  /**
   * サイトごとの最新記事一覧を取得します。
   *
   * @param siteId 取得する最新記事を持つサイトのID
   * @return 最新記事の一覧
   */
  def getLatelyBySiteId(siteId: Long): Seq[ArticleMapper] = {
    Redis.getOrElse[Seq[ArticleMapper]](s"latelyBySiteId:$siteId", 300) {
      ArticleMapper.findAllBy(sqls.eq(am.siteId, siteId).orderBy(ArticleMapper.am.createdAt).desc.limit(5)).toSeq
    }
  }
}
