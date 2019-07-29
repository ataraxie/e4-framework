#!/bin/bash
docker exec $(docker ps -qf "name=confluence-cluster-6153-node3") tail -fn 500 /confluence-home/logs/atlassian-confluence.log
