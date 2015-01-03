#!/bin/bash

mkdir -p conf/db/fixtures/default
echo "#!Ups" > conf/db/fixtures/default/table.sql
mysqldump -u root -d C_ANTENNA | sed '/*!/d' | sed '/--/d' >> conf/db/fixtures/default/table.sql
