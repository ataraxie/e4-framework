#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: createarchive.sh NAME"
  exit 0
fi
mkdir $1
mkdir $1/confluence-home
mkdir $1/confluence-shared-home

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/confluence.cfg.xml $1/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/index $1/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/journal $1/confluence-home/

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/confluence.cfg.xml $1/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/attachments $1/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/config $1/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/index-snapshots $1/confluence-shared-home/

docker exec $(docker ps -qf "name=confluence-cluster-6153-db") pg_dump -U confluence -Fc confluence > $1/confluencedb.tar.gz

tar cf $1.tar.gz $1

rm -r $1