#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Usage: createarchive-jira.sh NAME JIRA_VERSION_NO_DOTS"
  exit 1
fi
mkdir $1
mkdir $1/jira-home
mkdir $1/jira-shared-home

docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-home/dbconfig.xml $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-home/cluster.properties $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-home/data $1/jira-home/
docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-home/plugins $1/jira-home/

docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-shared-home/data $1/jira-shared-home/
docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-shared-home/plugins $1/jira-shared-home/

docker exec $(docker ps -qf "name=jira-cluster-$2-db") mysqldump jira > $1/jiradb.sql

tar cf $1.tar.gz $1

rm -r $1

aws s3 cp $1.tar.gz s3://e4prov/