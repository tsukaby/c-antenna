package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._

case class SiteSummaryMapper(
  id: Long, 
  name: String, 
  url: String, 
  thumbnail: Option[Array[Byte]] = None, 
  articleCount: Long, 
  clickCount: Long, 
  hatebuCount: Long) {

  def save()(implicit session: DBSession = SiteSummaryMapper.autoSession): SiteSummaryMapper = SiteSummaryMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SiteSummaryMapper.autoSession): Unit = SiteSummaryMapper.destroy(this)(session)

}
      

object SiteSummaryMapper extends SQLSyntaxSupport[SiteSummaryMapper] {

  override val tableName = "SITE_SUMMARY"

  override val columns = Seq("ID", "NAME", "URL", "THUMBNAIL", "ARTICLE_COUNT", "CLICK_COUNT", "HATEBU_COUNT")

  def apply(ssm: SyntaxProvider[SiteSummaryMapper])(rs: WrappedResultSet): SiteSummaryMapper = apply(ssm.resultName)(rs)
  def apply(ssm: ResultName[SiteSummaryMapper])(rs: WrappedResultSet): SiteSummaryMapper = new SiteSummaryMapper(
    id = rs.get(ssm.id),
    name = rs.get(ssm.name),
    url = rs.get(ssm.url),
    thumbnail = rs.get(ssm.thumbnail),
    articleCount = rs.get(ssm.articleCount),
    clickCount = rs.get(ssm.clickCount),
    hatebuCount = rs.get(ssm.hatebuCount)
  )
      
  val ssm = SiteSummaryMapper.syntax("ssm")

  override val autoSession = AutoSession

  def find(id: Long, name: String, url: String, thumbnail: Option[Array[Byte]], articleCount: Long, clickCount: Long, hatebuCount: Long)(implicit session: DBSession = autoSession): Option[SiteSummaryMapper] = {
    withSQL {
      select.from(SiteSummaryMapper as ssm).where.eq(ssm.id, id).and.eq(ssm.name, name).and.eq(ssm.url, url).and.eq(ssm.thumbnail, thumbnail).and.eq(ssm.articleCount, articleCount).and.eq(ssm.clickCount, clickCount).and.eq(ssm.hatebuCount, hatebuCount)
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
    thumbnail: Option[Array[Byte]] = None,
    articleCount: Long,
    clickCount: Long,
    hatebuCount: Long)(implicit session: DBSession = autoSession): SiteSummaryMapper = {
    withSQL {
      insert.into(SiteSummaryMapper).columns(
        column.id,
        column.name,
        column.url,
        column.thumbnail,
        column.articleCount,
        column.clickCount,
        column.hatebuCount
      ).values(
        id,
        name,
        url,
        thumbnail,
        articleCount,
        clickCount,
        hatebuCount
      )
    }.update.apply()

    SiteSummaryMapper(
      id = id,
      name = name,
      url = url,
      thumbnail = thumbnail,
      articleCount = articleCount,
      clickCount = clickCount,
      hatebuCount = hatebuCount)
  }

  def save(entity: SiteSummaryMapper)(implicit session: DBSession = autoSession): SiteSummaryMapper = {
    withSQL {
      update(SiteSummaryMapper).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.url -> entity.url,
        column.thumbnail -> entity.thumbnail,
        column.articleCount -> entity.articleCount,
        column.clickCount -> entity.clickCount,
        column.hatebuCount -> entity.hatebuCount
      ).where.eq(column.id, entity.id).and.eq(column.name, entity.name).and.eq(column.url, entity.url).and.eq(column.thumbnail, entity.thumbnail).and.eq(column.articleCount, entity.articleCount).and.eq(column.clickCount, entity.clickCount).and.eq(column.hatebuCount, entity.hatebuCount)
    }.update.apply()
    entity
  }
        
  def destroy(entity: SiteSummaryMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SiteSummaryMapper).where.eq(column.id, entity.id).and.eq(column.name, entity.name).and.eq(column.url, entity.url).and.eq(column.thumbnail, entity.thumbnail).and.eq(column.articleCount, entity.articleCount).and.eq(column.clickCount, entity.clickCount).and.eq(column.hatebuCount, entity.hatebuCount) }.update.apply()
  }
        
}
