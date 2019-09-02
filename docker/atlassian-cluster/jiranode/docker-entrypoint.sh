#!/bin/bash

set -e

umask u+rxw,g+rwx,o-rwx

# BEGIN: EDIT
# Given by --env: $E4_PROV_KEY, $E4_APP_VERSION, $E4_NODE_HEAP
E4_APP_VERSION_DOT_FREE=${E4_APP_VERSION//\./}
# END: EDIT

echo "+++++++++++++++++++++++++++++++++++++++++++++"
echo ">>> CUSTOM PROVISIONING ENTRYPOINT"
echo "+++++++++++++++++++++++++++++++++++++++++++++"

#
# PATCH SETENV.SH
#
echo ">>> Patching setenv.sh"
sed -i -e "s/export CATALINA_OPTS/CATALINA_OPTS=\"-Datlassian.webresource.disable.minification=true -Dupm.pac.disable=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 \${CATALINA_OPTS}\"\nexport CATALINA_OPTS/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh
sed -i -e "s/-Xms\${JVM_MINIMUM_MEMORY}/-Xms${E4_NODE_HEAP}m/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh
sed -i -e "s/-Xmx\${JVM_MAXIMUM_MEMORY}/-Xmx${E4_NODE_HEAP}m/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh

cp /e4prov/mysql-connector.jar /jira/atlassian-jira-software-latest-standalone/atlassian-jira/WEB-INF/lib

# RESTORE HOME DIR
if [[ "${NODE_NUMBER}" = "1" ]]
then
    echo ">>> Provisioning home dir"
    rm -rf /jira-home/*
    rm -rf /jira-shared-home/*
    if [[ -d /e4prov/$E4_PROV_KEY ]];
      then
      echo ">>> docker-entrypoint: provisioning home dir for $E4_PROV_KEY"
      cp -r /e4prov/$E4_PROV_KEY/jira-home/* /jira-home/ #2>/dev/null || :
      cp -r /e4prov/$E4_PROV_KEY/jira-shared-home/* /jira-shared-home/ #2>/dev/null || :
    else
      echo ">>> No provision dir found. Starting from scratch."
    fi
fi

# Create jira-config.properties
echo ">>> Create jira-config.properties with websudo disabled"
touch /jira-home/jira-config.properties
echo "jira.websudo.is.disabled = true" >> /jira-home/jira-config.properties
echo "jira.index.issue.threads = 40" >> /jira-home/jira-config.properties

#
# GENERATE CLUSTER CONF
#
env | j2  --format=env /work-private/cluster.properties.jinja2 > /jira-home/cluster.properties

echo ">>> Starting catalina.sh"
/jira/atlassian-jira-software-latest-standalone/bin/catalina.sh run
