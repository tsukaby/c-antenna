#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# フロントビルド
cd modules/layered-application
npm install
grunt clean
grunt setup
grunt
cd ../../

# サーバビルド
sbt clean stage
# リセット
# アプリ停止
ssh -t sakura2_app "/usr/local/app/c-antenna/run_play.sh stop"
ssh -t sakura2_app "sudo rm -rf /usr/local/app/c-antenna"

cp setup/run_play.sh target/universal/stage/

# 圧縮
cd target/universal/ && tar cvfz c-antenna.tar.gz stage && mv c-antenna.tar.gz ../../ && cd ../../

# 転送
scp -r c-antenna.tar.gz sakura2_app:/usr/local/app
rm -f c-antenna.tar.gz
# 解凍
ssh -t sakura2_app "rm -rf /usr/local/app/c-antenna ; tar xvzf /usr/local/app/c-antenna.tar.gz && mv ./stage /usr/local/app/c-antenna"
# 実行
ssh -t sakura2_app "cd /usr/local/app/c-antenna && chmod a+x run_play.sh && nohup ./run_play.sh start"
