package com.tsukaby.c_antenna.entity

import com.tsukaby.c_antenna.db.mapper.SiteMapper

object ImplicitConverter {
  implicit def dbSiteToSite(siteMapper: SiteMapper): Site = {
    Site(siteMapper.id, siteMapper.name, siteMapper.url)
  }

  implicit def dbSiteToSite(siteMapper: Option[SiteMapper]): Option[Site] = {
    siteMapper match {
      case Some(x) => Option(dbSiteToSite(x))
      case None => None
    }
  }

  implicit def dbSitesToSites(siteMappers: Seq[SiteMapper]): Seq[Site] = {
    siteMappers map (x => dbSiteToSite(x))
  }
}
