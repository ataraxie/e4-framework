#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Usage: createarchive-jira.sh NAME JIRA_VERSION_NO_DOTS"
  exit 1
fi
mkdir $1

docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-home $1/
docker cp $(docker ps -qf "name=jira-cluster-$2-node1"):/jira-shared-home $1/

docker exec $(docker ps -qf "name=jira-cluster-$2-db") mysqldump jira > $1/jiradb.sql

tar cf $1.tar.gz $1

rm -r $1

aws s3 cp $1.tar.gz s3://e4prov/