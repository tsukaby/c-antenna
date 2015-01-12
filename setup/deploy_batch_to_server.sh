#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# ビルド
sbt clean assembly

# 転送
scp ./c-antenna-batch.jar sakura2_app:/usr/local/app/c-antenna-batch/c-antenna-batch.jar.tmp
ssh -t sakura2_app "mv -f /usr/local/app/c-antenna-batch/c-antenna-batch.jar.tmp /usr/local/app/c-antenna-batch/c-antenna-batch.jar"
