package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class ArticleMapper(
  id: Long,
  siteId: Long,
  url: String,
  eyeCatchUrl: Option[String] = None,
  title: String,
  description: Option[String] = None,
  categoryId: Option[Long] = None,
  tag: Option[String] = None,
  clickCount: Long,
  hatebuCount: Long,
  publishedAt: DateTime) {

  def save()(implicit session: DBSession = ArticleMapper.autoSession): ArticleMapper = ArticleMapper.save(this)(session)

  def destroy()(implicit session: DBSession = ArticleMapper.autoSession): Unit = ArticleMapper.destroy(this)(session)

}


object ArticleMapper extends SQLSyntaxSupport[ArticleMapper] {

  override val tableName = "article"

  override val columns = Seq("id", "site_id", "url", "eye_catch_url", "title", "description", "category_id", "tag", "click_count", "hatebu_count", "published_at")

  def apply(am: SyntaxProvider[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = apply(am.resultName)(rs)
  def apply(am: ResultName[ArticleMapper])(rs: WrappedResultSet): ArticleMapper = new ArticleMapper(
    id = rs.get(am.id),
    siteId = rs.get(am.siteId),
    url = rs.get(am.url),
    eyeCatchUrl = rs.get(am.eyeCatchUrl),
    title = rs.get(am.title),
    description = rs.get(am.description),
    categoryId = rs.get(am.categoryId),
    tag = rs.get(am.tag),
    clickCount = rs.get(am.clickCount),
    hatebuCount = rs.get(am.hatebuCount),
    publishedAt = rs.get(am.publishedAt)
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
    description: Option[String] = None,
    categoryId: Option[Long] = None,
    tag: Option[String] = None,
    clickCount: Long,
    hatebuCount: Long,
    publishedAt: DateTime)(implicit session: DBSession = autoSession): ArticleMapper = {
    val generatedKey = withSQL {
      insert.into(ArticleMapper).columns(
        column.siteId,
        column.url,
        column.eyeCatchUrl,
        column.title,
        column.description,
        column.categoryId,
        column.tag,
        column.clickCount,
        column.hatebuCount,
        column.publishedAt
      ).values(
        siteId,
        url,
        eyeCatchUrl,
        title,
        description,
        categoryId,
        tag,
        clickCount,
        hatebuCount,
        publishedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ArticleMapper(
      id = generatedKey,
      siteId = siteId,
      url = url,
      eyeCatchUrl = eyeCatchUrl,
      title = title,
      description = description,
      categoryId = categoryId,
      tag = tag,
      clickCount = clickCount,
      hatebuCount = hatebuCount,
      publishedAt = publishedAt)
  }

  def save(entity: ArticleMapper)(implicit session: DBSession = autoSession): ArticleMapper = {
    withSQL {
      update(ArticleMapper).set(
        column.id -> entity.id,
        column.siteId -> entity.siteId,
        column.url -> entity.url,
        column.eyeCatchUrl -> entity.eyeCatchUrl,
        column.title -> entity.title,
        column.description -> entity.description,
        column.categoryId -> entity.categoryId,
        column.tag -> entity.tag,
        column.clickCount -> entity.clickCount,
        column.hatebuCount -> entity.hatebuCount,
        column.publishedAt -> entity.publishedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArticleMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ArticleMapper).where.eq(column.id, entity.id) }.update.apply()
  }

}
