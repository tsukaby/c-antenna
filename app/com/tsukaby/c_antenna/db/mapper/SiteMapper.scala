package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class SiteMapper(
  id: Long, 
  name: String, 
  url: String, 
  thumbnail: Option[Array[Byte]] = None) {

  def save()(implicit session: DBSession = SiteMapper.autoSession): SiteMapper = SiteMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SiteMapper.autoSession): Unit = SiteMapper.destroy(this)(session)

}
      

object SiteMapper extends SQLSyntaxSupport[SiteMapper] {

  override val tableName = "SITE"

  override val columns = Seq("ID", "NAME", "URL", "THUMBNAIL")

  def apply(sm: SyntaxProvider[SiteMapper])(rs: WrappedResultSet): SiteMapper = apply(sm.resultName)(rs)
  def apply(sm: ResultName[SiteMapper])(rs: WrappedResultSet): SiteMapper = new SiteMapper(
    id = rs.get(sm.id),
    name = rs.get(sm.name),
    url = rs.get(sm.url),
    thumbnail = rs.get(sm.thumbnail)
  )
      
  val sm = SiteMapper.syntax("sm")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[SiteMapper] = {
    withSQL {
      select.from(SiteMapper as sm).where.eq(sm.id, id)
    }.map(SiteMapper(sm.resultName)).single.apply()
  }
          
  def findAll()(implicit session: DBSession = autoSession): List[SiteMapper] = {
    withSQL(select.from(SiteMapper as sm)).map(SiteMapper(sm.resultName)).list.apply()
  }
          
  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(SiteMapper as sm)).map(rs => rs.long(1)).single.apply().get
  }
          
  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SiteMapper] = {
    withSQL { 
      select.from(SiteMapper as sm).where.append(sqls"${where}")
    }.map(SiteMapper(sm.resultName)).list.apply()
  }
      
  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL { 
      select(sqls"count(1)").from(SiteMapper as sm).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }
      
  def create(
    name: String,
    url: String,
    thumbnail: Option[Array[Byte]] = None)(implicit session: DBSession = autoSession): SiteMapper = {
    val generatedKey = withSQL {
      insert.into(SiteMapper).columns(
        column.name,
        column.url,
        column.thumbnail
      ).values(
        name,
        url,
        thumbnail
      )
    }.updateAndReturnGeneratedKey.apply()

    SiteMapper(
      id = generatedKey, 
      name = name,
      url = url,
      thumbnail = thumbnail)
  }

  def save(entity: SiteMapper)(implicit session: DBSession = autoSession): SiteMapper = {
    withSQL {
      update(SiteMapper).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.url -> entity.url,
        column.thumbnail -> entity.thumbnail
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }
        
  def destroy(entity: SiteMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SiteMapper).where.eq(column.id, entity.id) }.update.apply()
  }
        
}
