package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.db.entity.{SimpleSearchCondition, SortOrder}
import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import org.joda.time.DateTime
import scalikejdbc._

/**
 * 記事に関する操作を行います。
 */
trait ArticleDao extends BaseDao {

  private val am = ArticleMapper.am

  private val expireSeconds = 60 * 60 * 3

  /**
   * 記事を作成します。
   *
   * @param siteId サイトID
   * @param url 記事URL
   * @param eyeCatchUrl アイキャッチ画像URL
   * @param title タイトル
   * @param tags タグ
   * @param clickCount クリック数
   * @param hatebuCount はてぶ数
   * @param publishedAt 記事作成日時
   * @return 作成された記事
   */
  def create(
    siteId: Long,
    url: String,
    eyeCatchUrl: Option[String],
    title: String,
    description: Option[String],
    categoryId: Option[Long],
    tags: Option[String],
    clickCount: Long,
    hatebuCount: Long,
    publishedAt: DateTime): ArticleMapper = {
    val createdArticle = ArticleMapper.create(
      siteId,
      url,
      eyeCatchUrl,
      title,
      description,
      categoryId,
      tags,
      clickCount,
      hatebuCount,
      publishedAt)

    refreshCache(createdArticle)

    createdArticle
  }

  /**
   * 記事を取得します。
   *
   * @param id 取得する記事のID
   */
  def getById(id: Long): Option[ArticleMapper] = {
    VolatilityCache.getOrElse[Option[ArticleMapper]](s"article:$id", expireSeconds) {
      ArticleMapper.findAllBy(sqls.eq(am.id, id)).headOption
    }
  }

  /**
   * 記事を取得します。
   *
   * @param url 取得する記事のURL
   */
  def getByUrl(url: String): Option[ArticleMapper] = {
    VolatilityCache.getOrElse[Option[ArticleMapper]](s"article:$url", expireSeconds) {
      ArticleMapper.findAllBy(sqls.eq(am.url, url)).headOption
    }
  }

  /**
   * 検索条件に従って記事を取得します。
   *
   * @param condition 検索条件
   * @return 最新記事の一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[ArticleMapper] = DB readOnly { session =>
    VolatilityCache.getOrElse[Seq[ArticleMapper]](s"article:$condition", 300) {
      val sql = createSql(condition, withPaging = true)
      ArticleMapper.findAllBy(sql).toSeq
    }
  }

  def getAll: Seq[ArticleMapper] = DB readOnly { session =>
    ArticleMapper.findAll()
  }

  /**
   * 全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    ArticleMapper.countAll
  }

  /**
   * 記事の数を取得します。
   *
   * @param url 取得する記事のURL
   */
  def countByUrl(url: String): Long = {
    VolatilityCache.getOrElse[Long](s"articleCount:$url", expireSeconds) {
      ArticleMapper.countBy(sqls.eq(am.url, url))
    }
  }

  /**
   * 全体の件数を取得します。
   * @return 件数
   */
  def countByCondition(condition: SimpleSearchCondition): Long = DB readOnly { session =>
    VolatilityCache.getOrElse[Long](s"articleCount:$condition", 300) {
      val sql = createSql(condition, withPaging = false)
      ArticleMapper.countBy(sql)
    }
  }

  /**
   * サイトごとの最新記事一覧を取得します。
   *
   * @param siteId 取得する最新記事を持つサイトのID
   * @return 最新記事の一覧
   */
  def getLatelyBySiteId(siteId: Long): Seq[ArticleMapper] = {
    VolatilityCache.getOrElse[Seq[ArticleMapper]](s"latelyBySiteId:$siteId", expireSeconds) {
      ArticleMapper.findAllBy(sqls.eq(am.siteId, siteId).orderBy(am.publishedAt).desc.limit(5)).toSeq
    }
  }

  /**
   * 記事を更新します。
   * @param article 更新する記事。更新内容
   * @return 更新後の記事
   */
  def update(article: ArticleMapper): ArticleMapper = {
    val updated = article.save()
    refreshCache(article)

    updated
  }

  /**
   * キャッシュを再設定します。参照処理以外が発生したときに呼び出します。
   * @param article 更新や削除によって作られたオブジェクト
   */
  def refreshCache(article: ArticleMapper): Unit = {
    VolatilityCache.set(s"article:${article.id}", Some(article), expireSeconds)
    VolatilityCache.set(s"article:${article.url}", Some(article), expireSeconds)
    VolatilityCache.set(s"articleCount:${article.url}", 1L, expireSeconds)
  }

  /**
    * 記事を削除します。
    * @param condition 削除条件
    */
  def deleteBy(condition: ArticleDeleteCondition)(implicit session: DBSession = AutoSession): Unit = {
    withSQL {
      delete
        .from(ArticleMapper)
        .where(condition.toWhere)
    }.update().apply()
  }

  /**
   * 引数の条件に従ってSQLを作成します。
   * @param condition 検索条件・ソート条件・ページング条件
   * @param withPaging ページングの有無
   * @return SQLの一部
   */
  private def createSql(condition: SimpleSearchCondition, withPaging: Boolean): SQLSyntax = {

    var sql = sqls.eq(sqls"1", 1)

    // where
    sql = condition.maxId match {
      case Some(x) => sql.and.le(am.id, x)
      case None => sql
    }
    sql = condition.startDateTime match {
      case Some(x) => sql.and.gt(am.publishedAt, x)
      case None => sql
    }
    sql = condition.endDateTime match {
      case Some(x) => sql.and.lt(am.publishedAt, x)
      case None => sql
    }
    sql = condition.categoryId match {
      case Some(x) => sql.and.eq(am.categoryId, x)
      case None => sql
    }
    sql = condition.hasEyeCatch match {
      case true => sql.and.isNotNull(am.eyeCatchUrl)
      case false => sql
    }

    // order by
    sql = condition.sort match {
      case Some(x) =>
        if (x.order == SortOrder.Asc) {
          sql.orderBy(am.column(x.key)).asc
        } else {
          sql.orderBy(am.column(x.key)).desc
        }
      case None =>
        sql.orderBy(am.publishedAt).desc
    }

    // paging
    if (withPaging) {
      val page = condition.page.getOrElse(1)
      val count = condition.count.getOrElse(10)
      sql = sql.limit(count).offset((page - 1) * count)
    }

    sql
  }
}

object ArticleDao extends ArticleDao

case class ArticleDeleteCondition(
  publishedAtLessThan: Option[DateTime] = None
) {
  def toWhere: SQLSyntax = {
    sqls.lt(ArticleMapper.column.publishedAt, publishedAtLessThan)
  }
}