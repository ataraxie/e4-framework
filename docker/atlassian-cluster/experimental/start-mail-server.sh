#!/bin/bash
docker run \
  --net=confluence-cluster-701 \
  --net-alias=confluence-cluster-701-mail \
  --name mailcatcher \
  -p 1080:1080 -p 1025:1025 \
  -d schickling/mailcatcher