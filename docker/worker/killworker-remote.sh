#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: SSH_HOST_NAME"
else
    ssh -t $1 'docker kill $(docker ps -q)'
fi
