package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.VolatilityCache
import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.entity.SimpleSearchCondition
import scalikejdbc._

/**
 * サイトに関する操作を行います。
 */
object SiteDao {

  private val sm = SiteMapper.sm

  /**
   * サイトを取得します。
   *
   * @param id 取得するサイトのID
   */
  def getById(id: Long): Option[SiteMapper] = {
    VolatilityCache.getOrElse[Option[SiteMapper]](s"site:$id", 300) {
      SiteMapper.find(id)
    }
  }

  /**
   * 全てのサイトを取得します。
   *
   * @return サイトの一覧
   */
  def getAll: Seq[SiteMapper] = {
    SiteMapper.findAll()
    /*
    Redis.getOrElse[Seq[SiteMapper]](s"siteAll", 300) {
      SiteMapper.findAll()
    }*/
  }

  /**
   * クロールすべきサイトを取得します。
   * クロールすべきサイトとは前回クロールした日付が古いサイトのことです。
   *
   * @param amount 取得する件数
   * @return クロールすべきサイトの一覧
   */
  def getOldCrawledSite(amount: Int): Seq[SiteMapper] = {
    SiteMapper.findAll().sortWith(_.crawledAt.getMillis < _.crawledAt.getMillis).take(amount).toSeq
  }

  /**
   * ページを指定してサイトを取得します。
   * @param condition 検索条件
   * @return ページ一覧
   */
  def getByCondition(condition: SimpleSearchCondition): Seq[SiteMapper] = {
    val sql = createSql(condition)

    SiteMapper.findAllBy(sql).toSeq
  }

  /**
   * サイト全体の件数を取得します。
   * @return 件数
   */
  def countAll: Long = {
    SiteMapper.countAll()
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
    VolatilityCache.set(s"site:${site.id}", Some(site), 300)
    VolatilityCache.remove(s"siteAll")
  }

  // TODO 共通かできたら考える 多分引数変わるから無理
  private def createSql(condition: SimpleSearchCondition): SQLSyntax = {
    val page = condition.page.getOrElse(1)
    val count = condition.count.getOrElse(10)
    sqls.eq(sqls"1", 1).limit(count).offset((page - 1) * count)
  }

}
