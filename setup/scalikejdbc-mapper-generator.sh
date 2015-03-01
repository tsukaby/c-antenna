#!/bin/bash

cd modules/layered-infrastructure

# table
sbt "scalikejdbc-gen-all-force"

# view
sbt "scalikejdbc-gen-force SITE_SUMMARY SiteSummaryMapper"

cd ../../
