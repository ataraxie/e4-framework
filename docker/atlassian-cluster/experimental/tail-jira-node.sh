#!/bin/bash
docker exec $(docker ps -qf "name=jira-cluster-830-node$1") tail -fn 500 /jira-home/log/atlassian-jira.log