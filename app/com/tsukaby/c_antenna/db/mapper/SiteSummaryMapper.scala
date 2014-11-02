package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class SiteSummaryMapper(
  id: Long, 
  name: String, 
  url: String, 
  count: Long) {

  def save()(implicit session: DBSession = SiteSummaryMapper.autoSession): SiteSummaryMapper = SiteSummaryMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SiteSummaryMapper.autoSession): Unit = SiteSummaryMapper.destroy(this)(session)

}
      

object SiteSummaryMapper extends SQLSyntaxSupport[SiteSummaryMapper] {

  override val tableName = "SITE_SUMMARY"

  override val columns = Seq("ID", "NAME", "URL", "COUNT")

  def apply(ssm: SyntaxProvider[SiteSummaryMapper])(rs: WrappedResultSet): SiteSummaryMapper = apply(ssm.resultName)(rs)
  def apply(ssm: ResultName[SiteSummaryMapper])(rs: WrappedResultSet): SiteSummaryMapper = new SiteSummaryMapper(
    id = rs.get(ssm.id),
    name = rs.get(ssm.name),
    url = rs.get(ssm.url),
    count = rs.get(ssm.count)
  )
      
  val ssm = SiteSummaryMapper.syntax("ssm")

  override val autoSession = AutoSession

  def find(id: Long, name: String, url: String, count: Long)(implicit session: DBSession = autoSession): Option[SiteSummaryMapper] = {
    withSQL {
      select.from(SiteSummaryMapper as ssm).where.eq(ssm.id, id).and.eq(ssm.name, name).and.eq(ssm.url, url).and.eq(ssm.count, count)
    }.map(SiteSummaryMapper(ssm.resultName)).single.apply()
  }
          
  def findAll()(implicit session: DBSession = autoSession): List[SiteSummaryMapper] = {
    withSQL(select.from(SiteSummaryMapper as ssm)).map(SiteSummaryMapper(ssm.resultName)).list.apply()
  }
          
  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(SiteSummaryMapper as ssm)).map(rs => rs.long(1)).single.apply().get
  }
          
  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SiteSummaryMapper] = {
    withSQL { 
      select.from(SiteSummaryMapper as ssm).where.append(sqls"${where}")
    }.map(SiteSummaryMapper(ssm.resultName)).list.apply()
  }
      
  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL { 
      select(sqls"count(1)").from(SiteSummaryMapper as ssm).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }
      
  def create(
    id: Long,
    name: String,
    url: String,
    count: Long)(implicit session: DBSession = autoSession): SiteSummaryMapper = {
    withSQL {
      insert.into(SiteSummaryMapper).columns(
        column.id,
        column.name,
        column.url,
        column.count
      ).values(
        id,
        name,
        url,
        count
      )
    }.update.apply()

    SiteSummaryMapper(
      id = id,
      name = name,
      url = url,
      count = count)
  }

  def save(entity: SiteSummaryMapper)(implicit session: DBSession = autoSession): SiteSummaryMapper = {
    withSQL {
      update(SiteSummaryMapper).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.url -> entity.url,
        column.count -> entity.count
      ).where.eq(column.id, entity.id).and.eq(column.name, entity.name).and.eq(column.url, entity.url).and.eq(column.count, entity.count)
    }.update.apply()
    entity
  }
        
  def destroy(entity: SiteSummaryMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SiteSummaryMapper).where.eq(column.id, entity.id).and.eq(column.name, entity.name).and.eq(column.url, entity.url).and.eq(column.count, entity.count) }.update.apply()
  }
        
}
