package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class ArticleMapper(
  url: String, 
  siteId: Long, 
  title: String, 
  tag: Option[String] = None, 
  createdAt: DateTime) {

  def save()(implicit session: DBSession = ArticleMapper.autoSession): ArticleMapper = ArticleMapper.save(this)(session)

  def destroy()(implicit session: DBSession = ArticleMapper.autoSession): Unit = ArticleMapper.destroy(this)(session)

}
      

object ArticleMapper extends SQLSyntaxSupport[ArticleMapper] {

  override val tableName = "ARTICLE"

  override val columns = Seq("URL", "SITE_ID", "TITLE", "TAG", "CREATED_AT")

  def apply(am: SyntaxProvider[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = apply(am.resultName)(rs)
  def apply(am: ResultName[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = new ArticleMapper(
    url = rs.get(am.url),
    siteId = rs.get(am.siteId),
    title = rs.get(am.title),
    tag = rs.get(am.tag),
    createdAt = rs.get(am.createdAt)
  )
      
  val am = ArticleMapper.syntax("am")

  override val autoSession = AutoSession

  def find(url: String)(implicit session: DBSession = autoSession): Option[ArticleMapper] = {
    withSQL {
      select.from(ArticleMapper as am).where.eq(am.url, url)
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
    url: String,
    siteId: Long,
    title: String,
    tag: Option[String] = None,
    createdAt: DateTime)(implicit session: DBSession = autoSession): ArticleMapper = {
    withSQL {
      insert.into(ArticleMapper).columns(
        column.url,
        column.siteId,
        column.title,
        column.tag,
        column.createdAt
      ).values(
        url,
        siteId,
        title,
        tag,
        createdAt
      )
    }.update.apply()

    ArticleMapper(
      url = url,
      siteId = siteId,
      title = title,
      tag = tag,
      createdAt = createdAt)
  }

  def save(entity: ArticleMapper)(implicit session: DBSession = autoSession): ArticleMapper = {
    withSQL {
      update(ArticleMapper).set(
        column.url -> entity.url,
        column.siteId -> entity.siteId,
        column.title -> entity.title,
        column.tag -> entity.tag,
        column.createdAt -> entity.createdAt
      ).where.eq(column.url, entity.url)
    }.update.apply()
    entity
  }
        
  def destroy(entity: ArticleMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ArticleMapper).where.eq(column.url, entity.url) }.update.apply()
  }
        
}
