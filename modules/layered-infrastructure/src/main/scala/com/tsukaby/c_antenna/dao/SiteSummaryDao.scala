package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.db.entity.{SortOrder, SimpleSearchCondition}
import com.tsukaby.c_antenna.db.mapper.SiteSummaryMapper
import scalikejdbc._

/**
 * サイトに関する操作を行います。
 */
trait SiteSummaryDao {

  private val ssm = SiteSummaryMapper.ssm

  /**
   * ページを指定してサイトを取得します。
   * @param condition 検索条件
   * @return ページ一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[SiteSummaryMapper] = {
    VolatilityCache.getOrElse[Seq[SiteSummaryMapper]](s"siteSummary:${condition.toString}", 300) {
      val sql = createSql(condition, withPaging = true)

      SiteSummaryMapper.findAllBy(sql).toSeq

    }
  }

  /**
   * サイト全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    SiteSummaryMapper.countAll()
  }

  /**
   * ページを指定してサイトを取得します。
   * @param condition 検索条件
   * @return ページ一覧
   */
  def countByCondition(condition: SimpleSearchCondition): Long = {
    VolatilityCache.getOrElse[Long](s"siteSummaryCount:${condition.toString}", 300) {
      val sql = createSql(condition, withPaging = false)

      SiteSummaryMapper.countBy(sql)
    }
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
          sql.orderBy(ssm.column(x.key)).asc
        } else {
          sql.orderBy(ssm.column(x.key)).desc
        }
      case None =>
        sql.orderBy(ssm.hatebuCount).desc
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

object SiteSummaryDao extends SiteSummaryDao
