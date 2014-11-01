package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import com.tsukaby.c_antenna.entity.SimpleSearchCondition
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
   * 検索条件に従って記事を取得します。
   *
   * @param condition 検索条件
   * @return 最新記事の一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[ArticleMapper] = {

    val sql = createSql(condition)

    // 最新一覧はすぐにかわるため、キャッシュは短めに設定
    Redis.getOrElse[Seq[ArticleMapper]]("lately", 60) {
      ArticleMapper.findAllBy(sql).toSeq
    }
  }

  /**
   * 全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    ArticleMapper.countAll
  }

  /**
   * サイトごとの最新記事一覧を取得します。
   *
   * @param siteId 取得する最新記事を持つサイトのID
   * @return 最新記事の一覧
   */
  def getLatelyBySiteId(siteId: Long): Seq[ArticleMapper] = {
    Redis.getOrElse[Seq[ArticleMapper]](s"latelyBySiteId:$siteId", 300) {
      ArticleMapper.findAllBy(sqls.eq(am.siteId, siteId).orderBy(am.createdAt).desc.limit(5)).toSeq
    }
  }

  private def createSql(condition: SimpleSearchCondition): SQLSyntax = {
    val page = condition.page.getOrElse(1)
    val count = condition.count.getOrElse(10)
    sqls.eq(sqls"1", 1).orderBy(am.createdAt).desc.limit(count).offset((page - 1) * count)
  }
}
