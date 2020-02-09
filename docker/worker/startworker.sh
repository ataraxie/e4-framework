#!/bin/bash

E4_DIR="/tmp/e4"
E4_APP_LICENSE="$E4_APP_LICENSE"

if [ "$#" -ne 4 ]; then
    echo "Usage: e4run WORKER_PORT TARGET_SYSTEM_IP APP_NAME APP_VERSION_NODOTS"
else
    docker run -d \
        --add-host="$3-cluster-$4-lb:$2" \
        --shm-size=2048m \
        -v "$E4_DIR:$E4_DIR" \
        -p "$1:$1" \
        -e E4_PORT="$1" \
        -e E4_JAR_URL='https://e4prov.s3-us-west-2.amazonaws.com/e4-LATEST.jar' \
        -e E4_OUTPUT_DIR="$E4_DIR/out/$1" \
        -e E4_INPUT_DIR="$E4_DIR/in" \
        -e E4_APP_LICENSE="$E4_APP_LICENSE" \
        fgrund/e4worker:0.3
fi