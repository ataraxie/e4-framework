#!/bin/bash
ssh -t $1 'docker kill $(docker ps -q)'
