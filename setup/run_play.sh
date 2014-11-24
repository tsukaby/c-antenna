#!/bin/sh

sudo ./bin/c-antenna -Dconfig.resource=prod.conf -Dhttp.port=80 >/dev/null 2>&1 &
