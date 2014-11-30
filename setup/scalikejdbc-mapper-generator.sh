#!/bin/bash

cd modules/layered-infrastructure

# table
sbt "scalikejdbc-gen-force ARTICLE ArticleMapper"
sbt "scalikejdbc-gen-force SITE SiteMapper"

# view
sbt "scalikejdbc-gen-force SITE_SUMMARY SiteSummaryMapper"

cd ../../
