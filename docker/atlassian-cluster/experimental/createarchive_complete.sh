#!/bin/bash
if [ -z "$1" ]; then
  echo "Usage: createarchive.sh NAME"
  exit 0
fi

mkdir $1

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home $1/

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home $1/

docker exec $(docker ps -qf "name=confluence-cluster-6153-db") pg_dump -U confluence -Fc confluence > $1/confluencedb.tar.gz

tar cf $1.tar.gz $1

rm -r $1