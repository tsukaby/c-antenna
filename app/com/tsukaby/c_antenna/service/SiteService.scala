package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.db.mapper.SiteMapper
import com.tsukaby.c_antenna.entity.Site
import com.tsukaby.c_antenna.entity.ImplicitConverter._

object SiteService extends BaseService {
  def getAll: Seq[Site] = {
    SiteMapper.findAll()
  }

}
