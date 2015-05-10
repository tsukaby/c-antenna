package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.db.entity.{SortOrder, SimpleSearchCondition}
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import scalikejdbc._

/**
 * サイトに関する操作を行います。
 */
trait SiteDao {

  private val sm = SiteMapper.sm

  private val expireSeconds = 60 * 60 * 3

  /**
   * サイトを取得します。
   *
   * @param id 取得するサイトのID
   */
  def getById(id: Long): Option[SiteMapper] = {
    VolatilityCache.getOrElse[Option[SiteMapper]](s"site:$id", expireSeconds) {
      SiteMapper.find(id)
    }
  }

  /**
   * 全てのサイトを取得します。
   *
   * @return サイトの一覧
   */
  def getAll: Seq[SiteMapper] = {

    VolatilityCache.getOrElse[Seq[SiteMapper]](s"siteAll", expireSeconds) {
      SiteMapper.findAll().toSeq
    }
  }

  /**
   * ページを指定してサイトを取得します。
   * @param condition 検索条件
   * @return ページ一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[SiteMapper] = {
    val sql = createSql(condition, withPaging = true)

    SiteMapper.findAllBy(sql).toSeq
  }

  /**
   * サイト全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    VolatilityCache.getOrElse[Long](s"siteAllCount", expireSeconds) {
      SiteMapper.countAll()
    }
  }

  /**
   * 検索条件に合うサイト数を取得します。
   * @param condition 検索条件
   * @return サイト数
   */
  def countByCondition(condition: SimpleSearchCondition): Long = {
    VolatilityCache.getOrElse[Long](s"countByCondition:${condition.toString}", 300) {
      val sql = createSql(condition, withPaging = false)

      SiteMapper.countBy(sql)
    }
  }


  /**
   * サイトを更新します。
   * @param site 更新するサイトオブジェクト。更新内容
   * @return 更新後のサイト
   */
  def update(site: SiteMapper): SiteMapper = {
    val updated = site.save()
    refreshCache(site)

    updated
  }

  /**
   * キャッシュを削除します。参照処理以外が発生したときに呼び出します。
   * @param site 更新や削除によって作られたオブジェクト
   */
  def refreshCache(site: SiteMapper): Unit = {
    VolatilityCache.set(s"site:${site.id}", Some(site), expireSeconds)
    VolatilityCache.remove(s"siteAll")
    VolatilityCache.remove(s"siteAllCount")
  }

  /**
   * 引数の条件に従ってSQLを作成します。
   * @param condition 検索条件・ソート条件・ページング条件
   * @param withPaging ページングの有無
   * @return SQLの一部
   */
  private def createSql(condition: SimpleSearchCondition, withPaging: Boolean): SQLSyntax = {

    // where
    var sql = sqls.eq(sqls"1", 1)

    // order by
    sql = condition.sort match {
      case Some(x) =>
        if (x.order == SortOrder.Asc) {
          sql.orderBy(sm.column(x.key)).asc
        } else {
          sql.orderBy(sm.column(x.key)).desc
        }
      case None =>
        sql.orderBy(sm.hatebuCount).desc
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

object SiteDao extends SiteDao
