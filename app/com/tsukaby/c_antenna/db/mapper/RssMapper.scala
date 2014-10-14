package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class RssMapper(
  id: Long, 
  siteId: Long, 
  url: String, 
  title: String) {

  def save()(implicit session: DBSession = RssMapper.autoSession): RssMapper = RssMapper.save(this)(session)

  def destroy()(implicit session: DBSession = RssMapper.autoSession): Unit = RssMapper.destroy(this)(session)

}
      

object RssMapper extends SQLSyntaxSupport[RssMapper] {

  override val tableName = "RSS"

  override val columns = Seq("ID", "SITE_ID", "URL", "TITLE")

  def apply(rm: SyntaxProvider[RssMapper])(rs: WrappedResultSet): RssMapper = apply(rm.resultName)(rs)
  def apply(rm: ResultName[RssMapper])(rs: WrappedResultSet): RssMapper = new RssMapper(
    id = rs.get(rm.id),
    siteId = rs.get(rm.siteId),
    url = rs.get(rm.url),
    title = rs.get(rm.title)
  )
      
  val rm = RssMapper.syntax("rm")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[RssMapper] = {
    withSQL {
      select.from(RssMapper as rm).where.eq(rm.id, id)
    }.map(RssMapper(rm.resultName)).single.apply()
  }
          
  def findAll()(implicit session: DBSession = autoSession): List[RssMapper] = {
    withSQL(select.from(RssMapper as rm)).map(RssMapper(rm.resultName)).list.apply()
  }
          
  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(RssMapper as rm)).map(rs => rs.long(1)).single.apply().get
  }
          
  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[RssMapper] = {
    withSQL { 
      select.from(RssMapper as rm).where.append(sqls"${where}")
    }.map(RssMapper(rm.resultName)).list.apply()
  }
      
  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL { 
      select(sqls"count(1)").from(RssMapper as rm).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }
      
  def create(
    siteId: Long,
    url: String,
    title: String)(implicit session: DBSession = autoSession): RssMapper = {
    val generatedKey = withSQL {
      insert.into(RssMapper).columns(
        column.siteId,
        column.url,
        column.title
      ).values(
        siteId,
        url,
        title
      )
    }.updateAndReturnGeneratedKey.apply()

    RssMapper(
      id = generatedKey, 
      siteId = siteId,
      url = url,
      title = title)
  }

  def save(entity: RssMapper)(implicit session: DBSession = autoSession): RssMapper = {
    withSQL {
      update(RssMapper).set(
        column.id -> entity.id,
        column.siteId -> entity.siteId,
        column.url -> entity.url,
        column.title -> entity.title
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }
        
  def destroy(entity: RssMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(RssMapper).where.eq(column.id, entity.id) }.update.apply()
  }
        
}
