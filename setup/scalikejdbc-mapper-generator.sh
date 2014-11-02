#!/bin/bash

sbt "scalikejdbc-gen-force ARTICLE ArticleMapper"
sbt "scalikejdbc-gen-force SITE SiteMapper"

sbt "scalikejdbc-gen-force SITE_SUMMARY SiteSummaryMapper"
