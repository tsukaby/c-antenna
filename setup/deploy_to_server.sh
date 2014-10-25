#!/bin/bash

# SCPとSSHを使ってリモートサーバにアプリをデプロイします。

sbt clean stage
ssh sakura2 "sudo rm -rf ~/c-antenna"
scp -r target/universal/stage sakura2:~/c-antenna
ssh sakura2 "sudo ./c-antenna/bin/c-antenna -Dhttp.port=80 >/dev/null 2>&1 &"
