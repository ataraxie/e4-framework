#!/bin/bash

mkdir archive
mkdir archive/confluence-home
mkdir archive/confluence-shared-home

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/confluence.cfg.xml archive/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/index archive/confluence-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-home/journal archive/confluence-home/

docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/confluence.cfg.xml archive/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/attachments archive/confluence-shared-home/
docker cp $(docker ps -qf "name=confluence-cluster-6153-node1"):/confluence-shared-home/config archive/confluence-shared-home/

docker exec $(docker ps -qf "name=confluence-cluster-6153-db") pg_dump -U confluence -Fc confluence > archive/confluencedb.tar.gz

tar cf $1.tar.gz archive/*

rm -r archive