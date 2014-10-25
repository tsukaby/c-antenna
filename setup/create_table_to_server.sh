#!/bin/bash

# SCPとSSHを使ってリモートサーバのテーブル定義を最新にします。

scp conf/db/fixtures/default/table.sql sakura2:~/
ssh sakura2 "mysql -u root -e'create database if not exists C_ANTENNA'"
ssh sakura2 "mysql -u root C_ANTENNA < ~/table.sql"
