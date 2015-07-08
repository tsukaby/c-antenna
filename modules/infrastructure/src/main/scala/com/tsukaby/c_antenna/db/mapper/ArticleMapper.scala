package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class ArticleMapper(
  id: Long,
  siteId: Long,
  url: String,
  eyeCatchUrl: Option[String] = None,
  title: String,
  tag: Option[String] = None,
  clickCount: Long,
  publishedAt: DateTime,
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None) {

  def save()(implicit session: DBSession = ArticleMapper.autoSession): ArticleMapper = ArticleMapper.save(this)(session)

  def destroy()(implicit session: DBSession = ArticleMapper.autoSession): Unit = ArticleMapper.destroy(this)(session)

}


object ArticleMapper extends SQLSyntaxSupport[ArticleMapper] {

  override val tableName = "article"

  override val columns = Seq("id", "site_id", "url", "eye_catch_url", "title", "tag", "click_count", "published_at", "created_at", "updated_at")

  def apply(am: SyntaxProvider[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = apply(am.resultName)(rs)
  def apply(am: ResultName[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = new ArticleMapper(
    id = rs.get(am.id),
    siteId = rs.get(am.siteId),
    url = rs.get(am.url),
    eyeCatchUrl = rs.get(am.eyeCatchUrl),
    title = rs.get(am.title),
    tag = rs.get(am.tag),
    clickCount = rs.get(am.clickCount),
    publishedAt = rs.get(am.publishedAt),
    createdAt = rs.get(am.createdAt),
    updatedAt = rs.get(am.updatedAt)
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
    withSQL(select(sqls.count).from(ArticleMapper as am)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ArticleMapper] = {
    withSQL {
      select.from(ArticleMapper as am).where.append(where)
    }.map(ArticleMapper(am.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ArticleMapper] = {
    withSQL {
      select.from(ArticleMapper as am).where.append(where)
    }.map(ArticleMapper(am.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ArticleMapper as am).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    siteId: Long,
    url: String,
    eyeCatchUrl: Option[String] = None,
    title: String,
    tag: Option[String] = None,
    clickCount: Long,
    publishedAt: DateTime,
    createdAt: Option[DateTime] = None,
    updatedAt: Option[DateTime] = None)(implicit session: DBSession = autoSession): ArticleMapper = {
    val generatedKey = withSQL {
      insert.into(ArticleMapper).columns(
        column.siteId,
        column.url,
        column.eyeCatchUrl,
        column.title,
        column.tag,
        column.clickCount,
        column.publishedAt,
        column.createdAt,
        column.updatedAt
      ).values(
        siteId,
        url,
        eyeCatchUrl,
        title,
        tag,
        clickCount,
        publishedAt,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ArticleMapper(
      id = generatedKey,
      siteId = siteId,
      url = url,
      eyeCatchUrl = eyeCatchUrl,
      title = title,
      tag = tag,
      clickCount = clickCount,
      publishedAt = publishedAt,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def save(entity: ArticleMapper)(implicit session: DBSession = autoSession): ArticleMapper = {
    withSQL {
      update(ArticleMapper).set(
        column.id -> entity.id,
        column.siteId -> entity.siteId,
        column.url -> entity.url,
        column.eyeCatchUrl -> entity.eyeCatchUrl,
        column.title -> entity.title,
        column.tag -> entity.tag,
        column.clickCount -> entity.clickCount,
        column.publishedAt -> entity.publishedAt,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArticleMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ArticleMapper).where.eq(column.id, entity.id) }.update.apply()
  }

}
