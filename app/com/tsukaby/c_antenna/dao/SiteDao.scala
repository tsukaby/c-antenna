package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.db.mapper.SiteMapper
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
    Redis.getOrElse[Option[SiteMapper]](s"site:$id", 300) {
      SiteMapper.find(id)
    }
  }

  /**
   * 全てのサイトを取得します。
   *
   * @return サイトの一覧
   */
  def getAll: Seq[SiteMapper] = {
    Redis.getOrElse[Seq[SiteMapper]](s"siteAll", 300) {
      SiteMapper.findAll()
    }
  }

  /**
   * クロールすべきサイトを取得します。
   * クロールすべきサイトとは前回クロールした日付が古いサイトのことです。
   *
   * @param amount 取得する件数
   * @return クロールすべきサイトの一覧
   */
  def getOldCrawledSite(amount: Int) = {
    SiteMapper.findAll().sortWith(_.crawledAt.getMillis < _.crawledAt.getMillis).take(amount)
  }

  /**
   * ページを指定してサイトを取得します。
   * @param page ページ番号 (1 origin)
   * @param count 取得件数
   * @return ページ一覧
   */
  def getWithPaging(page: Int, count: Int): Seq[SiteMapper] = {
    SiteMapper.findAllBy(sqls.eq(sqls"1", 1).limit(count).offset((page - 1) * count))
  }

  def update(site: SiteMapper): SiteMapper = {
    val updated = site.save()
    refreshCache(site)

    updated
  }

  private def refreshCache(site: SiteMapper) = {
    Redis.set(s"site:${site.id}", Some(site), 300)
    Redis.remove(s"siteAll")
  }

}
