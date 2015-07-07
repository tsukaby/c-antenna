package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class SiteMapper(
  id: Long,
  name: String,
  url: String,
  rssUrl: String,
  thumbnail: Option[Array[Byte]] = None,
  scrapingCssSelector: String,
  clickCount: Long,
  hatebuCount: Long,
  crawledAt: DateTime) {

  def save()(implicit session: DBSession = SiteMapper.autoSession): SiteMapper = SiteMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SiteMapper.autoSession): Unit = SiteMapper.destroy(this)(session)

}


object SiteMapper extends SQLSyntaxSupport[SiteMapper] {

  override val tableName = "site"

  override val columns = Seq("id", "name", "url", "rss_url", "thumbnail", "scraping_css_selector", "click_count", "hatebu_count", "crawled_at")

  def apply(sm: SyntaxProvider[SiteMapper])(rs: WrappedResultSet): SiteMapper = apply(sm.resultName)(rs)
  def apply(sm: ResultName[SiteMapper])(rs: WrappedResultSet): SiteMapper = new SiteMapper(
    id = rs.get(sm.id),
    name = rs.get(sm.name),
    url = rs.get(sm.url),
    rssUrl = rs.get(sm.rssUrl),
    thumbnail = rs.get(sm.thumbnail),
    scrapingCssSelector = rs.get(sm.scrapingCssSelector),
    clickCount = rs.get(sm.clickCount),
    hatebuCount = rs.get(sm.hatebuCount),
    crawledAt = rs.get(sm.crawledAt)
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
    withSQL(select(sqls.count).from(SiteMapper as sm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[SiteMapper] = {
    withSQL {
      select.from(SiteMapper as sm).where.append(where)
    }.map(SiteMapper(sm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SiteMapper] = {
    withSQL {
      select.from(SiteMapper as sm).where.append(where)
    }.map(SiteMapper(sm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(SiteMapper as sm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String,
    url: String,
    rssUrl: String,
    thumbnail: Option[Array[Byte]] = None,
    scrapingCssSelector: String,
    clickCount: Long,
    hatebuCount: Long,
    crawledAt: DateTime)(implicit session: DBSession = autoSession): SiteMapper = {
    val generatedKey = withSQL {
      insert.into(SiteMapper).columns(
        column.name,
        column.url,
        column.rssUrl,
        column.thumbnail,
        column.scrapingCssSelector,
        column.clickCount,
        column.hatebuCount,
        column.crawledAt
      ).values(
        name,
        url,
        rssUrl,
        thumbnail,
        scrapingCssSelector,
        clickCount,
        hatebuCount,
        crawledAt
      )
    }.updateAndReturnGeneratedKey.apply()

    SiteMapper(
      id = generatedKey,
      name = name,
      url = url,
      rssUrl = rssUrl,
      thumbnail = thumbnail,
      scrapingCssSelector = scrapingCssSelector,
      clickCount = clickCount,
      hatebuCount = hatebuCount,
      crawledAt = crawledAt)
  }

  def save(entity: SiteMapper)(implicit session: DBSession = autoSession): SiteMapper = {
    withSQL {
      update(SiteMapper).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.url -> entity.url,
        column.rssUrl -> entity.rssUrl,
        column.thumbnail -> entity.thumbnail,
        column.scrapingCssSelector -> entity.scrapingCssSelector,
        column.clickCount -> entity.clickCount,
        column.hatebuCount -> entity.hatebuCount,
        column.crawledAt -> entity.crawledAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: SiteMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SiteMapper).where.eq(column.id, entity.id) }.update.apply()
  }

}
