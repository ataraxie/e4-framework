#!/bin/bash
docker logs --follow $(docker ps -q)
