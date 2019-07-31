#!/bin/bash
ssh $1  'bash -s' < startworker-local.sh $2 $3
sleep 3
ssh -t $1 'docker logs --follow $(docker ps -q)'
