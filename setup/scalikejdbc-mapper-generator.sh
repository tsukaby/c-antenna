#!/bin/bash

cd modules/infrastructure

# table
sbt "scalikejdbc-gen-all-force"

# view

cd ../../
