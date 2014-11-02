#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# ビルド
sbt clean stage
# リセット
ssh -t sakura2_app "if [[ -e c-antenna/RUNNING_PID ]]; then sudo kill `cat c-antenna/RUNNING_PID`; else echo NOT RUNNING; fi; sudo rm -rf ~/c-antenna"

cp setup/run_play.sh target/universal/stage/

# 圧縮
cd target/universal/ && tar cvfz c-antenna.tar.gz stage && mv c-antenna.tar.gz ../../ && cd ../../

# 転送
scp -r c-antenna.tar.gz sakura2_app:~/
rm -f c-antenna.tar.gz
# 解凍
ssh -t sakura2_app "rm -rf c-antenna ; tar xvzf c-antenna.tar.gz && mv stage c-antenna"
# 実行
ssh -t sakura2_app "cd c-antenna && chmod a+x run_play.sh && nohup ./run_play.sh"
