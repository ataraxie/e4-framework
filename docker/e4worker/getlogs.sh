#!/bin/bash
ssh -t $1 'docker logs  $(docker ps -q)' > "confluence-log-$(date +%s).log"
ssh -t e4s 'rm e4.log'
ssh -t e4s 'docker cp $(docker ps -qf "name=confluence-cluster-6153-lb"):/var/www/logs/e4.log .'
ssh -t e4s 'grep /rest e4.log > e4-rest.log'
scp e4s:e4-rest.log "access-log-$(date +%s).log"
scp e4w:/tmp/e4/out/$2/e4\*.sqlite e4.sqlite
ssh -t e4w 'sudo rm /tmp/e4/out/$2/e4\*.sqlite'