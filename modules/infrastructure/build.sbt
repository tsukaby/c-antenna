scalikejdbcSettings

scalikejdbc.mapper.SbtKeys.scalikejdbcGeneratorSettings in Compile ~= {
  c => c.copy(tableNameToClassName = x => c.tableNameToClassName(x) + "Mapper")
}
