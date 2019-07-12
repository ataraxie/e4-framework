#!/bin/bash

set -e

umask u+rxw,g+rwx,o-rwx

# BEGIN: EDIT
# Given by --env: $E4_PROV_KEY, $CONFLUENCE_VERSION
CONFLUENCE_VERSION_DOT_FREE=${CONFLUENCE_VERSION//\./}
# END: EDIT

#
# FIND OUT IP ADDRESS
#
#IP_ADDRESS="127.0.0.1"
IP_ADDRESS=$(/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')

#
# PREDEFINED VARS
#
export JWT_PRIVATE_KEY="MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKEogp6FVTEq+vaplbSfsDbO4b9ednmwHKl2EIFvo10wPjiZ/K9CrlM8QRJjPkdH46+civWw06Hsu1fNaPWhj8LKqtkJ8QusvrTKykW03hAwWE6CAnjSHkj b+kNN4ptKpJ4bCYFhGZpOV3rbdXnr8wCtevB8feAvAK64kJsEOZzdAgMBAAECgYEAj/MqcUQxq4BzuN4Tvcoh0WML3C8Zbmqzv16ZMbSxXGzaNx68ySOrqOeaTD1fhLYfF16h9QGkl+9oC+6LwVQ1nuLCrFJxNEyE/7DSTv4D9PB+ny5Mii1WMNlKfFRhhKDzmannn3F1SD8 FEXuDcS9jyTA52qmTpqoz95o3NBFknAECQQDUhwCmHLZTBXczLNBEBaq09a0voYkwjAdW5CbteJBTaQ5uiDPt112f9z0JL6ltyGO7KONpmnbeZ2J/L0+itBXdAkEAwh+QR9EsOOFCJl0QnKodk51NQuqZ0BvaHVPVzsEAwJxpJyUAUhFVVw0r1z7NG9cycDkwr9XFPTkVuaD D4WyzAQJBAIO2R0ircrNxJ7anh0sg1/Lebz6dthBIOCQ5sYZqucd3zGHkN4qndna1GzaeOzq2flh3triz6gdbu0dnYstLIGECQQC2lg2VHy9jCKy5fMuFL5TGJSxohlTKI4hSEWqHH43fnL5i7TCSAG+ug1r7B7zQNObiG0ip+n2cijbe9FGJlD4BAkEAqau/IgUa+OMSdH4 bRfYejoysaQySdwXuCs/L30KeD2d2hfqJh/+5K8pHUg8weZ3yl28qh7LXuuiibCUJ2z5tFg=="
export JWT_PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChKIKehVUxKvr2qZW0n7A2zuG/XnZ5sBypdhCBb6NdMD44mfyvQq5TPEESYz5HR+OvnIr1sNOh7LtXzWj1oY/CyqrZCfELrL60yspFtN4QMFhOggJ40h5I2/pDTeKbSqSe GwmBYRmaTld623V56/MArXrwfH3gLwCuuJCbBDmc3QIDAQAB"
export MULTICAST_CLUSTER_ADDRESS="230.0.0.1"
export DATABASE_HOST="confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-db"
export DATABASE_USER="confluence"
export DATABASE_PASS="confluence"
export DATABASE_DB="confluence"
export LB_NAME="confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-lb"
export LB_PORT="2${CONFLUENCE_VERSION_DOT_FREE}"

#
# SYNCHRONY VARS
#
export PATH_TO_SYNCHRONY_STANDALONE_JAR="/confluence/atlassian-confluence-latest/confluence/WEB-INF/packages/synchrony-standalone.jar"
POSTGRESQL_JAR_FILE=$(ls /confluence/atlassian-confluence-latest/confluence/WEB-INF/lib/ | grep postgres)
export JDBC_DRIVER_PATH="/confluence/atlassian-confluence-latest/confluence/WEB-INF/lib/"$POSTGRESQL_JAR_FILE
export SYNCHRONY_PORT="8091"
export CLUSTER_LISTEN_PORT="5701"
export CLUSTER_BASE_PORT="25500"
export MULTICAST_GROUP=$MULTICAST_CLUSTER_ADDRESS
export SERVER_IP=$IP_ADDRESS
export SYNCHRONY_URL="http://confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-lb:2${CONFLUENCE_VERSION_DOT_FREE}/synchrony"

#
# PATCH SETENV.SH
#
NODE_ID="node${NODE_NUMBER}"
sed -i -e "s/export CATALINA_OPTS/#replaced/g" /confluence/atlassian-confluence-latest/bin/setenv.sh
echo -e "CATALINA_OPTS=\"-Dconfluence.cluster.node.name=${NODE_ID} \${CATALINA_OPTS}\"\n" >> /confluence/atlassian-confluence-latest/bin/setenv.sh
echo -e "CATALINA_OPTS=\"-Dsynchrony.service.url=http://confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-lb:${CONFLUENCE_VERSION_DOT_FREE}/synchrony/v1 \${CATALINA_OPTS}\"\n" >> /confluence/atlassian-confluence-latest/bin/setenv.sh
echo -e "-Dcom.sun.management.jmxremote=true \
         -Dcom.sun.management.jmxremote.port=9090 \
         -Dcom.sun.management.jmxremote.ssl=false \
         -Dcom.sun.management.jmxremote.authenticate=false" >> /confluence/atlassian-confluence-latest/bin/setenv.sh
#"-Djava.rmi.server.hostname=confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-lb" >> /confluence/atlassian-confluence-latest/bin/setenv.sh

echo -e "\nexport CATALINA_OPTS" >> /confluence/atlassian-confluence-latest/bin/setenv.sh

# BEGIN: edit
sed -i 's/Xmx1024/Xmx2048/g' /confluence/atlassian-confluence-latest/bin/setenv.sh
sed -i 's/Xms1024/Xms2048/g' /confluence/atlassian-confluence-latest/bin/setenv.sh
# END: edit

#
# PATCH server.xml FOR PROXY USE
#
sed -i -e "s/port=\"8090\"/port=\"8090\" proxyName=\"${LB_NAME}\" proxyPort=\"${LB_PORT}\" scheme=\"http\"/g" /confluence/atlassian-confluence-latest/conf/server.xml

#
# START SYNCHRONY
#
echo ">> docker-entrypoint: starting synchrony"
env | j2 --format=env /work-private/run-synchrony-jar.sh.jinja2 > /work-private/run-synchrony-jar.sh
bash /work-private/run-synchrony-jar.sh


#
# CONFLUENCE-HOME SYNC ON NODE CREATION
#
if [[ "${NODE_NUMBER}" != "1" ]]
then
  #
  # NODE 2...n
  #
  echo ">> docker-entrypoint: syncing confluence home from node1 ... please wait ..."
  curl --connect-timeout 180 --max-time 180 --fail -o /tmp/confluence-home.tar  http://confluence-cluster-${CONFLUENCE_VERSION_DOT_FREE}-node1:8888/download
  tar xfv /tmp/confluence-home.tar -C /
  chown -R worker:worker /confluence-home
else
  #
  # NODE 1
  #
  echo ">> docker-entrypoint: starting confluence home sync server on port 8888"
  #BEGIN: edit
  if [[ -d /e4prov/$E4_PROV_KEY ]];
  then
      echo ">>> docker-entrypoint: provisioning home dir for $E4_PROV_KEY"
      cp -r /e4prov/$E4_PROV_KEY/confluence-home/* /confluence-home/
      cp -r /e4prov/$E4_PROV_KEY/confluence-shared-home/* /confluence-shared-home/
  else
     echo ">>> No provision dir found. Starting from scratch."
  fi
  #END: edit
  python /work-private/confluence-home-sync-server.py &
fi

/confluence/atlassian-confluence-latest/bin/catalina.sh run
