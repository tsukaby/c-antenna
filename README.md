# カテゴリアンテナ

develop : [![Build Status](https://travis-ci.org/tsukaby/c-antenna.svg?branch=develop)](https://travis-ci.org/tsukaby/c-antenna)

master  : [![Build Status](https://travis-ci.org/tsukaby/c-antenna.svg?branch=master)](https://travis-ci.org/tsukaby/c-antenna)

climate : [![Code Climate](https://codeclimate.com/github/tsukaby/c-antenna/badges/gpa.svg)](https://codeclimate.com/github/tsukaby/c-antenna)

### Setup

    brew install mysql
    brew install nodejs
    brew install ruby
    gem install -g compass
    node install -g grunt-cli

### Install

    cd modules/web
    npm install
    bower install
    grunt setup
    grunt
    cd ../../

### Run

    sbt run

#### Batch
    
    sbt batch/run
    
### Test

    sbt test
    cd modules/web
    grunt test

### Deploy

Run the jenkins job.  
[c-antenna-packaging](http://jenkins.tsukaby.com/job/c-antenna-packaging/)

### memo

my.cnf

    # BLOBデータ等を受け付ける為、許容サイズをデフォルト16MBから以下へ変更
    max_allowed_packet=100MB
    # 圧縮を利用する為、Barracudaを設定
    innodb_file_per_table
    innodb_file_format = Barracuda
    innodb_log_file_size=256M


圧縮するテーブルには`ROW_FORMAT=COMPRESSED`を付ける

### Update dao

    cd modules/infrastructure
    sbt scalikejdbc-gen-all-force

### Update

    sbt dependencyUpdates

    npm install -g david
    david update
    
    npm install -g tsd
    tsd update -so