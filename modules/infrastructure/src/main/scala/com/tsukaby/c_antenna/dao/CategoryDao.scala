package com.tsukaby.c_antenna.dao

import com.tsukaby.c_antenna.cache.VolatilityCache
import com.tsukaby.c_antenna.db.mapper.CategoriesMapper
import scalikejdbc._

/**
 * カテゴリに関する操作を行います。
 */
trait CategoryDao {

  private val cm = CategoriesMapper.cm

  private val expireSeconds = 60 * 60 * 3

  /**
   * カテゴリを取得します。
   *
   * @param id 取得するカテゴリのID
   */
  def getById(id: Long): Option[CategoriesMapper] = {
    VolatilityCache.getOrElse[Option[CategoriesMapper]](s"category:$id", expireSeconds) {
      CategoriesMapper.findAllBy(sqls.eq(cm.id, id)).headOption
    }
  }

  /**
   * カテゴリを取得します。
   *
   * @param name 取得するカテゴリの名前
   */
  def getByName(name: String): Option[CategoriesMapper] = {
    VolatilityCache.getOrElse[Option[CategoriesMapper]](s"category:$name", expireSeconds) {
      CategoriesMapper.findAllBy(sqls.eq(cm.name, name)).headOption
    }
  }
}

object CategoryDao extends CategoryDao
