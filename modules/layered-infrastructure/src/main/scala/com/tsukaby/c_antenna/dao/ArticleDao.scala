package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.db.mapper.ArticleMapper
import com.tsukaby.c_antenna.entity.{SimpleSearchCondition, SortOrder}
import org.joda.time.DateTime
import scalikejdbc._

import scalaz.Scalaz._

/**
 * 記事に関する操作を行います。
 */
trait ArticleDao {

  private val am = ArticleMapper.am

  /**
   * 記事を作成します。
   *
   * @param siteId サイトID
   * @param url 記事URL
   * @param title タイトル
   * @param tags タグ
   * @param clickCount クリック数
   * @param createdAt 記事作成日時
   * @return 作成された記事
   */
  def create(siteId: Long, url: String, title: String, tags: Option[String], clickCount: Long, createdAt: DateTime): ArticleMapper = {
    val createdArticle = ArticleMapper.create(siteId, url, title, tags, clickCount, createdAt)

    refreshCache(createdArticle)

    createdArticle
  }

  /**
   * 記事を取得します。
   *
   * @param id 取得する記事のID
   */
  def getById(id: Long): Option[ArticleMapper] = {
    VolatilityCache.getOrElse[Option[ArticleMapper]](s"article:$id", 300) {
      ArticleMapper.findAllBy(sqls.eq(am.id, id)).headOption
    }
  }

  /**
   * 記事を取得します。
   *
   * @param url 取得する記事のURL
   */
  def getByUrl(url: String): Option[ArticleMapper] = {
    VolatilityCache.getOrElse[Option[ArticleMapper]](s"article:$url", 300) {
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
    val sql = createSql(condition, withPaging = true)
    // cache keyが難しいので一旦キャッシュは保留
    ArticleMapper.findAllBy(sql).toSeq
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
    VolatilityCache.getOrElse[Long](s"articleCount:$url", 300) {
      ArticleMapper.countBy(sqls.eq(am.url, url))
    }
  }

  /**
   * 全体の件数を取得します。
   * @return 件数
   */
  def countByCondition(condition: SimpleSearchCondition): Long = DB readOnly { session =>
    val sql = createSql(condition, withPaging = false)
    ArticleMapper.countBy(sql)
  }

  /**
   * サイトごとの最新記事一覧を取得します。
   *
   * @param siteId 取得する最新記事を持つサイトのID
   * @return 最新記事の一覧
   */
  def getLatelyBySiteId(siteId: Long): Seq[ArticleMapper] = {
    VolatilityCache.getOrElse[Seq[ArticleMapper]](s"latelyBySiteId:$siteId", 300) {
      ArticleMapper.findAllBy(sqls.eq(am.siteId, siteId).orderBy(am.createdAt).desc.limit(5)).toSeq
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
    VolatilityCache.set(s"article:${article.id}", article.some, 300)
    VolatilityCache.set(s"article:${article.url}", article.some, 300)
    VolatilityCache.set(s"articleCount:${article.url}", 1L, 300)
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
    sql = condition.startDateTime match {
      case Some(x) => sql.and.gt(am.createdAt, x)
      case None => sql
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
        sql.orderBy(am.createdAt).desc
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
