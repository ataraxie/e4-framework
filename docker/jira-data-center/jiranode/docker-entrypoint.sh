#!/bin/bash

set -e

umask u+rxw,g+rwx,o-rwx

echo "+++++++++++++++++++++++++++++++++++++++++++++"
echo ">>> CUSTOM PROVISIONING ENTRYPOINT"
echo "+++++++++++++++++++++++++++++++++++++++++++++"

#
# PATCH SETENV.SH
#
echo ">>> Patching setenv.sh"
sed -i -e "s/export CATALINA_OPTS/CATALINA_OPTS=\"-Datlassian.webresource.disable.minification=true -Dupm.pac.disable=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 \${CATALINA_OPTS}\"\nexport CATALINA_OPTS/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh
sed -i -e "s/JVM_MINIMUM_MEMORY=\"384m\"/JVM_MINIMUM_MEMORY=\"4096m\"/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh
sed -i -e "s/JVM_MAXIMUM_MEMORY=\"768m\"/JVM_MAXIMUM_MEMORY=\"4096m\"/g" /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh
cat /jira/atlassian-jira-software-latest-standalone/bin/setenv.sh

# RESTORE HOME DIR
if [[ "${NODE_NUMBER}" = "1" ]]
then
    echo ">>> Provisioning home dir"
    cp -r /work-my/provision/jira-home-7.12.0/* /jira-home/
    cp -r /work-my/provision/jira-shared-home-7.12.0/* /jira-shared-home/
fi

# Create jira-config.properties
echo ">>> Create jira-config.properties with websudo disabled"
touch /jira-home/jira-config.properties
echo "jira.websudo.is.disabled = true" >> /jira-home/jira-config.properties


#
# GENERATE CLUSTER CONF
#
env | j2  --format=env /work-private/cluster.properties.jinja2 > /jira-home/cluster.properties

echo ">>> Starting catalina.sh"
/jira/atlassian-jira-software-latest-standalone/bin/catalina.sh run