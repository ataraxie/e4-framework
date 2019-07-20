#!/bin/bash
ssh -t $1 'docker logs --follow $(docker ps -q)'
