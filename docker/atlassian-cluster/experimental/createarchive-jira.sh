#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: createarchive-jira.sh NAME"
  exit 0
fi
mkdir $1
mkdir $1/jira-home
mkdir $1/jira-shared-home

docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-home/dbconfig.xml $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-home/cluster.properties $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-home/data $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-home/plugins $1/jira-home/

docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-shared-home/data $1/jira-shared-home/
docker cp $(docker ps -qf "name=jira-cluster-830-node1"):/jira-shared-home/plugins $1/jira-shared-home/

docker exec $(docker ps -qf "name=jira-cluster-830-db") pg_dump -U jira -Fc jira > $1/jiradb.tar.gz

tar cf $1.tar.gz $1

rm -r $1

aws s3 cp $1.tar.gz s3://e4prov/