#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Usage: createarchive-confluence.sh NAME CONFLUENCE_VERSION_NO_DOTS"
  exit 1
fi
mkdir $1
mkdir $1/confluence-home
mkdir $1/confluence-shared-home

echo "Copying confluence-home/"
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-home/confluence.cfg.xml $1/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-home/index $1/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-home/journal $1/confluence-home/

echo "Copying confluence-shared-home/"
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-shared-home/confluence.cfg.xml $1/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-shared-home/attachments $1/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-shared-home/config $1/confluence-shared-home/
if [[ -d "/confluence-shared-home/index-snapshots" ]]; then
  docker cp $(docker ps -qf "name=confluence-cluster-$2-node1"):/confluence-shared-home/index-snapshots $1/confluence-shared-home/
fi

echo "Creating database dump"
docker exec $(docker ps -qf "name=confluence-cluster-$2-db") pg_dump -U confluence -Fc confluence > $1/confluencedb.tar.gz

tar cf $1.tar.gz $1

rm -r $1

echo "Uploading to S3 bucket 's3://e4prov/'"
aws s3 cp $1.tar.gz s3://e4prov/