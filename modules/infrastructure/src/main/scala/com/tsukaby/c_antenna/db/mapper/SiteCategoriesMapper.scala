package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class SiteCategoriesMapper(
  siteId: Long,
  categoryId: Long) {

  def save()(implicit session: DBSession = SiteCategoriesMapper.autoSession): SiteCategoriesMapper = SiteCategoriesMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SiteCategoriesMapper.autoSession): Unit = SiteCategoriesMapper.destroy(this)(session)

}


object SiteCategoriesMapper extends SQLSyntaxSupport[SiteCategoriesMapper] {

  override val tableName = "site_categories"

  override val columns = Seq("site_id", "category_id")

  def apply(scm: SyntaxProvider[SiteCategoriesMapper])(rs: WrappedResultSet): SiteCategoriesMapper = apply(scm.resultName)(rs)
  def apply(scm: ResultName[SiteCategoriesMapper])(rs: WrappedResultSet): SiteCategoriesMapper = new SiteCategoriesMapper(
    siteId = rs.get(scm.siteId),
    categoryId = rs.get(scm.categoryId)
  )

  val scm = SiteCategoriesMapper.syntax("scm")

  override val autoSession = AutoSession

  def find(categoryId: Long, siteId: Long)(implicit session: DBSession = autoSession): Option[SiteCategoriesMapper] = {
    withSQL {
      select.from(SiteCategoriesMapper as scm).where.eq(scm.categoryId, categoryId).and.eq(scm.siteId, siteId)
    }.map(SiteCategoriesMapper(scm.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[SiteCategoriesMapper] = {
    withSQL(select.from(SiteCategoriesMapper as scm)).map(SiteCategoriesMapper(scm.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(SiteCategoriesMapper as scm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[SiteCategoriesMapper] = {
    withSQL {
      select.from(SiteCategoriesMapper as scm).where.append(where)
    }.map(SiteCategoriesMapper(scm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SiteCategoriesMapper] = {
    withSQL {
      select.from(SiteCategoriesMapper as scm).where.append(where)
    }.map(SiteCategoriesMapper(scm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(SiteCategoriesMapper as scm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    categoryId: Long)(implicit session: DBSession = autoSession): SiteCategoriesMapper = {
    val generatedKey = withSQL {
      insert.into(SiteCategoriesMapper).columns(
        column.categoryId
      ).values(
        categoryId
      )
    }.updateAndReturnGeneratedKey.apply()

    SiteCategoriesMapper(
      siteId = generatedKey,
      categoryId = categoryId)
  }

  def save(entity: SiteCategoriesMapper)(implicit session: DBSession = autoSession): SiteCategoriesMapper = {
    withSQL {
      update(SiteCategoriesMapper).set(
        column.siteId -> entity.siteId,
        column.categoryId -> entity.categoryId
      ).where.eq(column.categoryId, entity.categoryId).and.eq(column.siteId, entity.siteId)
    }.update.apply()
    entity
  }

  def destroy(entity: SiteCategoriesMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SiteCategoriesMapper).where.eq(column.categoryId, entity.categoryId).and.eq(column.siteId, entity.siteId) }.update.apply()
  }

}
