#!/bin/bash

# SCPとSSHを使ってリモートサーバのテーブル定義を最新にします。

scp conf/db/fixtures/default/table.sql sakura2_app:~/
ssh sakura2_app "mysql -u root -e'create database if not exists C_ANTENNA'"
ssh sakura2_app "mysql -u root C_ANTENNA < ~/table.sql"
