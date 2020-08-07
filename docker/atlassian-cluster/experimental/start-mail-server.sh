#!/bin/bash
docker run \
  --net=confluence \
  --net-alias=confluence-mail \
  --name mailcatcher \
  -p 1080:1080 -p 1025:1025 \
  -d schickling/mailcatcher