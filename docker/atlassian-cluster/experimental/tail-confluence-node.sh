#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Usage: tail-confluence-node.sh NODE_NUMBER CONFLUENCE_VERSION_NO_DOTS"
  exit 1
fi
docker exec $(docker ps -qf "name=confluence-cluster-$2-node$1") tail -fn 500 /confluence-home/logs/atlassian-confluence.log