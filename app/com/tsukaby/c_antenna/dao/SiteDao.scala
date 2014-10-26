package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.db.mapper.SiteMapper
import play.api.cache.Cache
import play.api.Play.current

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
    Cache.getOrElse[Option[SiteMapper]](s"site:$id", 300) {
      SiteMapper.find(id)
    }
  }

  /**
   * 全てのサイトを取得します。
   *
   * @return サイトの一覧
   */
  def getAll: Seq[SiteMapper] = {
    Cache.getOrElse[Seq[SiteMapper]](s"siteAll", 300) {
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

  def update(site: SiteMapper) = {
    site.save()
    refreshCache(site)
  }

  private def refreshCache(site: SiteMapper) = {
    Cache.set(s"site:${site.id}", Some(site), 300)
    Cache.remove(s"siteAll")
  }

}
