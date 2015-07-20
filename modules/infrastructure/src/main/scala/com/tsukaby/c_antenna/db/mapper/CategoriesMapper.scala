package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class CategoriesMapper(
  id: Long,
  name: String) {

  def save()(implicit session: DBSession = CategoriesMapper.autoSession): CategoriesMapper = CategoriesMapper.save(this)(session)

  def destroy()(implicit session: DBSession = CategoriesMapper.autoSession): Unit = CategoriesMapper.destroy(this)(session)

}


object CategoriesMapper extends SQLSyntaxSupport[CategoriesMapper] {

  override val tableName = "categories"

  override val columns = Seq("id", "name")

  def apply(cm: SyntaxProvider[CategoriesMapper])(rs: WrappedResultSet): CategoriesMapper = apply(cm.resultName)(rs)
  def apply(cm: ResultName[CategoriesMapper])(rs: WrappedResultSet): CategoriesMapper = new CategoriesMapper(
    id = rs.get(cm.id),
    name = rs.get(cm.name)
  )

  val cm = CategoriesMapper.syntax("cm")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[CategoriesMapper] = {
    withSQL {
      select.from(CategoriesMapper as cm).where.eq(cm.id, id)
    }.map(CategoriesMapper(cm.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CategoriesMapper] = {
    withSQL(select.from(CategoriesMapper as cm)).map(CategoriesMapper(cm.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CategoriesMapper as cm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CategoriesMapper] = {
    withSQL {
      select.from(CategoriesMapper as cm).where.append(where)
    }.map(CategoriesMapper(cm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CategoriesMapper] = {
    withSQL {
      select.from(CategoriesMapper as cm).where.append(where)
    }.map(CategoriesMapper(cm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CategoriesMapper as cm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String)(implicit session: DBSession = autoSession): CategoriesMapper = {
    val generatedKey = withSQL {
      insert.into(CategoriesMapper).columns(
        column.name
      ).values(
        name
      )
    }.updateAndReturnGeneratedKey.apply()

    CategoriesMapper(
      id = generatedKey,
      name = name)
  }

  def save(entity: CategoriesMapper)(implicit session: DBSession = autoSession): CategoriesMapper = {
    withSQL {
      update(CategoriesMapper).set(
        column.id -> entity.id,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: CategoriesMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(CategoriesMapper).where.eq(column.id, entity.id) }.update.apply()
  }

}
