#!/bin/bash
docker exec $(docker ps -qf "name=jira-cluster-7120-node1") tail -fn 500 /jira-home/log/atlassian-jira.log
