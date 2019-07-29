#!/bin/bash

set -e

umask u+rxw,g+rwx,o-rwx

#
# GENERATE LOADBALANCER CONFIG BASED ON AMOUNT OF NODES
#
echo "generating loadbalancer config for $NODES nodes"
env | j2 --format=env /work-private/loadbalancer-virtual-host.conf.jinja2 > /work-private/loadbalancer-virtual-host.conf

# BEGIN: EDIT
echo "LogFormat \"%{ms}T|%U\" custom" >> /etc/apache2/httpd.conf
echo "CustomLog logs/e4.log custom" >> /etc/apache2/httpd.conf
# END: EDIT

httpd -DFOREGROUND