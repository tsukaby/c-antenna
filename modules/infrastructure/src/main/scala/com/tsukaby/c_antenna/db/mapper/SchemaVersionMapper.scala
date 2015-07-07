package com.tsukaby.c_antenna.db.mapper

import scalikejdbc._
import org.joda.time.{DateTime}

case class SchemaVersionMapper(
  versionRank: Int,
  installedRank: Int,
  version: String,
  description: String,
  `type`: String,
  script: String,
  checksum: Option[Int] = None,
  installedBy: String,
  installedOn: DateTime,
  executionTime: Int,
  success: Boolean) {

  def save()(implicit session: DBSession = SchemaVersionMapper.autoSession): SchemaVersionMapper = SchemaVersionMapper.save(this)(session)

  def destroy()(implicit session: DBSession = SchemaVersionMapper.autoSession): Unit = SchemaVersionMapper.destroy(this)(session)

}


object SchemaVersionMapper extends SQLSyntaxSupport[SchemaVersionMapper] {

  override val tableName = "schema_version"

  override val columns = Seq("version_rank", "installed_rank", "version", "description", "type", "script", "checksum", "installed_by", "installed_on", "execution_time", "success")

  def apply(svm: SyntaxProvider[SchemaVersionMapper])(rs: WrappedResultSet): SchemaVersionMapper = apply(svm.resultName)(rs)
  def apply(svm: ResultName[SchemaVersionMapper])(rs: WrappedResultSet): SchemaVersionMapper = new SchemaVersionMapper(
    versionRank = rs.get(svm.versionRank),
    installedRank = rs.get(svm.installedRank),
    version = rs.get(svm.version),
    description = rs.get(svm.description),
    `type` = rs.get(svm.`type`),
    script = rs.get(svm.script),
    checksum = rs.get(svm.checksum),
    installedBy = rs.get(svm.installedBy),
    installedOn = rs.get(svm.installedOn),
    executionTime = rs.get(svm.executionTime),
    success = rs.get(svm.success)
  )

  val svm = SchemaVersionMapper.syntax("svm")

  override val autoSession = AutoSession

  def find(version: String)(implicit session: DBSession = autoSession): Option[SchemaVersionMapper] = {
    withSQL {
      select.from(SchemaVersionMapper as svm).where.eq(svm.version, version)
    }.map(SchemaVersionMapper(svm.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[SchemaVersionMapper] = {
    withSQL(select.from(SchemaVersionMapper as svm)).map(SchemaVersionMapper(svm.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(SchemaVersionMapper as svm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[SchemaVersionMapper] = {
    withSQL {
      select.from(SchemaVersionMapper as svm).where.append(where)
    }.map(SchemaVersionMapper(svm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SchemaVersionMapper] = {
    withSQL {
      select.from(SchemaVersionMapper as svm).where.append(where)
    }.map(SchemaVersionMapper(svm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(SchemaVersionMapper as svm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    versionRank: Int,
    installedRank: Int,
    version: String,
    description: String,
    `type`: String,
    script: String,
    checksum: Option[Int] = None,
    installedBy: String,
    installedOn: DateTime,
    executionTime: Int,
    success: Boolean)(implicit session: DBSession = autoSession): SchemaVersionMapper = {
    withSQL {
      insert.into(SchemaVersionMapper).columns(
        column.versionRank,
        column.installedRank,
        column.version,
        column.description,
        column.`type`,
        column.script,
        column.checksum,
        column.installedBy,
        column.installedOn,
        column.executionTime,
        column.success
      ).values(
        versionRank,
        installedRank,
        version,
        description,
        `type`,
        script,
        checksum,
        installedBy,
        installedOn,
        executionTime,
        success
      )
    }.update.apply()

    SchemaVersionMapper(
      versionRank = versionRank,
      installedRank = installedRank,
      version = version,
      description = description,
      `type` = `type`,
      script = script,
      checksum = checksum,
      installedBy = installedBy,
      installedOn = installedOn,
      executionTime = executionTime,
      success = success)
  }

  def save(entity: SchemaVersionMapper)(implicit session: DBSession = autoSession): SchemaVersionMapper = {
    withSQL {
      update(SchemaVersionMapper).set(
        column.versionRank -> entity.versionRank,
        column.installedRank -> entity.installedRank,
        column.version -> entity.version,
        column.description -> entity.description,
        column.`type` -> entity.`type`,
        column.script -> entity.script,
        column.checksum -> entity.checksum,
        column.installedBy -> entity.installedBy,
        column.installedOn -> entity.installedOn,
        column.executionTime -> entity.executionTime,
        column.success -> entity.success
      ).where.eq(column.version, entity.version)
    }.update.apply()
    entity
  }

  def destroy(entity: SchemaVersionMapper)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(SchemaVersionMapper).where.eq(column.version, entity.version) }.update.apply()
  }

}
