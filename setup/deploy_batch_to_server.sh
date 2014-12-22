#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

# ビルド
sbt clean assembly

# 転送
scp target/scala-2.11/c-antenna-assembly-1.0.jar sakura2_app:/usr/local/app/c-antenna-batch/c-antenna-batch-1.0.jar
