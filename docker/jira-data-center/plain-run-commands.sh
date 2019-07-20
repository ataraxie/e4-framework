#!/bin/bash
docker run \
    --rm \
    --name jira-cluster-7120-node1 \
    --net=jira-cluster-7120 \
    --net-alias=jira-cluster-7120-node1 \
    --env NODE_NUMBER=1 \
    -v jira-shared-home-7120:/jira-shared-home \
    -p 5006:5006 \
    -v $(pwd)/jiranode:/work-my \
    --entrypoint /work-my/docker-entrypoint.sh \
    -d codeclou/docker-atlassian-jira-data-center:jiranode-7.12.0

docker run \
    --rm \
    --name jira-cluster-7120-db \
    --net=jira-cluster-7120 \
    --net-alias=jira-cluster-7120-db \
    -e POSTGRES_PASSWORD=jira \
    -e POSTGRES_USER=jira \
    -v $(pwd)/postgres:/work-my \
    -v $(pwd)/postgres/provision:/docker-entrypoint-initdb.d \
    -it postgres:9.4
