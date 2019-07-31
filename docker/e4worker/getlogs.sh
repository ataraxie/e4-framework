#!/bin/bash
echo $3
ssh -t $2 'docker logs  $(docker ps -q)' > "worker-log-$(date +%s).log"
ssh -t $1 'rm e4.log'
ssh -t $1 'docker cp $(docker ps -qf "name=confluence-cluster-6153-lb"):/var/www/logs/e4.log .'
ssh -t $1 'grep /rest e4.log > e4-rest.log'
scp $1:e4-rest.log "access-log-$(date +%s).log"
scp $2:/tmp/e4/out/$3/e4\*.sqlite e4.sqlite
ssh -t $2 "mv /tmp/e4/out/$3/*.sqlite /tmp/e4/out/"