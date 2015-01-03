package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class ArticleMapper(
  id: Long, 
  siteId: Long, 
  url: String, 
  title: String, 
  tag: Option[String] = None, 
  clickCount: Long, 
  createdAt: DateTime) {

  def save()(implicit session: DBSession = ArticleMapper.autoSession): ArticleMapper = ArticleMapper.save(this)(session)

  def destroy()(implicit session: DBSession = ArticleMapper.autoSession): Unit = ArticleMapper.destroy(this)(session)

}
      

object ArticleMapper extends SQLSyntaxSupport[ArticleMapper] {

  override val tableName = "ARTICLE"

  override val columns = Seq("ID", "SITE_ID", "URL", "TITLE", "TAG", "CLICK_COUNT", "CREATED_AT")

  def apply(am: SyntaxProvider[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = apply(am.resultName)(rs)
  def apply(am: ResultName[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = new ArticleMapper(
    id = rs.get(am.id),
    siteId = rs.get(am.siteId),
    url = rs.get(am.url),
    title = rs.get(am.title),
    tag = rs.get(am.tag),
    clickCount = rs.get(am.clickCount),
    createdAt = rs.get(am.createdAt)
  )
      
  val am = ArticleMapper.syntax("am")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[ArticleMapper] = {
    withSQL {
      select.from(ArticleMapper as am).where.eq(am.id, id)
    }.map(ArticleMapper(am.resultName)).single.apply()
  }
          
  def findAll()(implicit session: DBSession = autoSession): List[ArticleMapper] = {
    withSQL(select.from(ArticleMapper as am)).map(ArticleMapper(am.resultName)).list.apply()
  }
          
  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ArticleMapper as am)).map(rs => rs.long(1)).single.apply().get
  }
          
  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ArticleMapper] = {
    withSQL { 
      select.from(ArticleMapper as am).where.append(sqls"${where}")
    }.map(ArticleMapper(am.resultName)).list.apply()
  }
      
  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL { 
      select(sqls"count(1)").from(ArticleMapper as am).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }
      
  def create(
    siteId: Long,
    url: String,
    title: String,
    tag: Option[String] = None,
    clickCount: Long,
    createdAt: DateTime)(implicit session: DBSession = autoSession): ArticleMapper = {
    val generatedKey = withSQL {
      insert.into(ArticleMapper).columns(
        column.siteId,
        column.url,
        column.title,
        column.tag,
        column.clickCount,
        column.createdAt
      ).values(
        siteId,
        url,
        title,
        tag,
        clickCount,
        createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ArticleMapper(
      id = generatedKey, 
      siteId = siteId,
      url = url,
      title = title,
      tag = tag,
      clickCount = clickCount,
      createdAt = createdAt)
  }

  def save(entity: ArticleMapper)(implicit session: DBSession = autoSession): ArticleMapper = {
    withSQL {
      update(ArticleMapper).set(
        column.id -> entity.id,
        column.siteId -> entity.siteId,
        column.url -> entity.url,
        column.title -> entity.title,
        column.tag -> entity.tag,
        column.clickCount -> entity.clickCount,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }
        
  def destroy(entity: ArticleMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ArticleMapper).where.eq(column.id, entity.id) }.update.apply()
  }
        
}
