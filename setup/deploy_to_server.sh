#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# ビルド
sbt clean stage
# リセット
ssh -t sakura2 "sudo kill `cat c-antenna/RUNNING_PID`; sudo rm -rf ~/c-antenna"
# 転送
scp -r target/universal/stage sakura2:~/c-antenna
# 実行
ssh -t sakura2 "cd c-antenna ; nohup sudo ./bin/c-antenna -Dhttp.port=80 >/dev/null 2>&1 &"
