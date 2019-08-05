#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Usage: tail-jira-node.sh NODE_NUMBER JIRA_VERSION_NO_DOTS"
  exit 1
fi
docker exec $(docker ps -qf "name=jira-cluster-$2-node$1") tail -fn 500 /jira-home/log/atlassian-jira.log