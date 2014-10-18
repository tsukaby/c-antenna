#!/bin/bash

sbt "scalikejdbc-gen-force ARTICLE ArticleMapper"
sbt "scalikejdbc-gen-force RSS RssMapper"
sbt "scalikejdbc-gen-force SITE SiteMapper"