#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# ビルド
sbt clean stage
# リセット
ssh -t sakura2 "if [[ -e c-antenna/RUNNING_PID ]]; then sudo kill `cat c-antenna/RUNNING_PID`; else echo NOT RUNNING; fi; sudo rm -rf ~/c-antenna"
# 転送
scp -r target/universal/stage sakura2:~/c-antenna
scp -r setup/run_play.sh sakura2:~/c-antenna
# 実行
ssh -t sakura2 "cd c-antenna ; chmod a+x run_play.sh ; nohup ./run_play.sh"
