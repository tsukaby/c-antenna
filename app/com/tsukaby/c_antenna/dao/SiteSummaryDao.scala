package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.db.mapper.SiteSummaryMapper
import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import scalikejdbc._

/**
 * サイトに関する操作を行います。
 */
object SiteSummaryDao {

  private val ssm = SiteSummaryMapper.ssm

  /**
   * ページを指定してサイトを取得します。
   * @param condition 検索条件
   * @return ページ一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[SiteSummaryMapper] = {
    val sql = createSql(condition)

    SiteSummaryMapper.findAllBy(sql).toSeq
  }

  /**
   * サイト全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    SiteSummaryMapper.countAll()
  }

  // TODO 共通かできたら考える 多分引数変わるから無理
  private def createSql(condition: SimpleSearchCondition): SQLSyntax = {
    val page = condition.page.getOrElse(1)
    val count = condition.count.getOrElse(10)
    sqls.eq(sqls"1", 1).limit(count).offset((page - 1) * count)
  }

}
