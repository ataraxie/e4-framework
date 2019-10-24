#!/bin/bash

set -e

POSTGRESQL_VERSION="9.6"
MYSQL_VERSION="5.7"

export CLICOLOR=1
C_RED='\x1B[31m'
C_CYN='\x1B[96m'
C_GRN='\x1B[32m'
C_MGN='\x1B[35m'
C_RST='\x1B[39m'

return_by_reference() {
	if unset -v "$1"; then
		eval $1=\"\$2\"
	fi
}

contains() {
	string="$1"
	substring="$2"
	if test "${string#*$substring}" != "$string"
	then
		return 0		# $substring is in $string
	else
		return 1		# $substring is not in $string
	fi
}

wait_for_logs_to_contain() {
	while : ; do
		log_content=$(docker logs $(docker ps -qf "name=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-$1") 2>&1)
		if contains "$log_content" "$2"
		then
		echo ""
		break
		else
		echo -n "."
		sleep 1
		fi
	done
}

function _is_named_container_running {
	local ret_value=$(docker ps --format '{{.ID}}' --filter "name=${1}" | wc -l | awk '{print $1}')

	local "$2" && return_by_reference $2 $ret_value
}

function _kill_and_remove_named_instance_if_exists {
	container_name=$1
	# KILL
	named_container_running_result=-1
	_is_named_container_running ${container_name} named_container_running_result
	if (( named_container_running_result == 1 )) # arithmetic brackets ... woohoo
	then
		echo -e $C_CYN">> docker kill ........:${C_RST}${C_GRN} Killing${C_RST}	 - Named container ${container_name} is running."
		docker kill ${container_name}
	else
		echo -e $C_CYN">> docker kill ........:${C_RST}${C_MGN} Skipping${C_RST}	- Named container ${container_name} is not running."
	fi
}

function kill_instance_loadbalancer {
	_kill_and_remove_named_instance_if_exists ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb
}

function start_instance_database {
	echo -e $C_CYN">> docker run .........:${C_RST}${C_GRN} Starting${C_RST}	- Starting instance ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db."
	docker run \
		--rm \
		--cpu-shares=512 \
		--sysctl kernel.shmmax=100663296 \
		--name ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db \
		--net=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE} \
		--net-alias=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db \
		--env POSTGRES_PASSWORD=${E4_APP_NAME} \
		--env POSTGRES_USER=${E4_APP_NAME} \
		--env E4_PROV_KEY=$E4_PROV_KEY \
		--env E4_APP_NAME=$E4_APP_NAME \
		-v $(pwd)/postgres:/docker-entrypoint-initdb.d \
		-v $E4_PROV_DIR:/e4prov \
		-d postgres:${POSTGRESQL_VERSION} -c max_connections=350 -c shared_buffers=2GB -c effective_cache_size=8GB -c checkpoint_timeout=5min -c wal_level=minimal -c autovacuum=off
}

function start_instance_database_mysql {
	echo -e $C_CYN">> docker run .........:${C_RST}${C_GRN} Starting${C_RST}	- Starting instance ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db."
	docker run \
		--rm \
		--name ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db \
		--net=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE} \
		--net-alias=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db \
		--env MYSQL_ALLOW_EMPTY_PASSWORD=yes \
		--env MYSQL_PASSWORD=${E4_APP_NAME} \
		--env MYSQL_USER=${E4_APP_NAME} \
		--env MYSQL_DATABASE=${E4_APP_NAME} \
		--env E4_PROV_KEY=$E4_PROV_KEY \
		--env E4_APP_NAME=$E4_APP_NAME \
		-v $(pwd)/mysql:/docker-entrypoint-initdb.d \
		-v $E4_PROV_DIR:/e4prov \
		-d mysql:${MYSQL_VERSION} \
		--transaction-isolation=READ-COMMITTED \
		--max-allowed-packet=512M \
		--default-storage-engine=INNODB \
		--character-set-server=utf8mb4 \
		--innodb-log-file-size=500M
}

function start_instance_loadbalancer {
	echo -e $C_CYN">> docker run .........:${C_RST}${C_GRN} Starting${C_RST}	- Starting instance ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb."
	docker run \
		--rm \
		--name ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb \
		--net=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE} \
		--net-alias=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb \
		--env NODES=${1} \
		-v $(pwd)/loadbalancer:/e4work \
		--entrypoint /e4work/docker-entrypoint.sh \
		-p $E4_LB_PUBLIC_PORT:$E4_LB_PUBLIC_PORT \
		-d codeclou/docker-atlassian-${E4_APP_NAME}-data-center:loadbalancer-${E4_APP_VERSION}
}

function start_instance_node {
	echo -e $C_CYN">> docker run .........:${C_RST}${C_GRN} Starting${C_RST}	- Starting instance ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node${1}."
	docker run \
		--rm \
		--name ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node${1} \
		--net=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE} \
		--net-alias=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node${1} \
		--env NODE_NUMBER=${1} \
		--env E4_PROV_KEY=$E4_PROV_KEY \
		--env E4_PROV_DIR=$E4_PROV_DIR \
		--env E4_NODE_HEAP=$E4_NODE_HEAP \
		--env E4_APP_VERSION=$E4_APP_VERSION \
		--env E4_APP_VERSION_DOTFREE=$E4_APP_VERSION_DOTFREE \
		--env E4_LB_PUBLIC_PORT=$E4_LB_PUBLIC_PORT \
		-v ${E4_APP_NAME}-shared-home-${E4_APP_VERSION_DOTFREE}:/${E4_APP_NAME}-shared-home \
		#-p "$(($1 + 2))${E4_APP_VERSION_DOTFREE}:500$1" \
		-p "${E4_APP_VERSION_DOTFREE}$1:433$1" \
		-v $(pwd)/${E4_APP_NAME}node:/e4work \
		-v $E4_PROV_DIR:/e4prov \
		--entrypoint /e4work/docker-entrypoint.sh \
		-d codeclou/docker-atlassian-${E4_APP_NAME}-data-center:${E4_APP_NAME}node-${E4_APP_VERSION}
}

function download_curl_fail_if_unavailable {
	echo ">>> Download via CURL: $1"
	mkdir -p "$E4_PROV_DIR/$E4_PROV_KEY"
	curl -f -o "$E4_PROV_DIR/$2" "$1"
	if [[ $? -eq 0 ]]
	then
	  echo "Download successful"
	else
	  echo "Download failed. Exiting."
	  exit 1
	fi
}

function download_synchrony {
	echo ">>> Download synchrony jar via AWS"
	mkdir -p "$E4_PROV_DIR/$E4_PROV_KEY"
	aws s3 cp s3://e4prov/synchrony-standalone.jar $E4_PROV_DIR/
}

function download_mysql_connector {
	echo ">>> Download mysql connector via AWS"
	mkdir -p "$E4_PROV_DIR/$E4_PROV_KEY"
	aws s3 cp s3://e4prov/mysql-connector.jar $E4_PROV_DIR/
}

function download_app_after_success {
	  echo ">>> Output file: $E4_PROV_DIR/$E4_PROV_KEY.tar.gz"
    echo ">>> Extracting archive"
		tar xf $E4_PROV_DIR/$E4_PROV_KEY.tar.gz -C $E4_PROV_DIR
		if [ "$E4_APP_NAME" = "confluence" ]; then
			cp $E4_PROV_DIR/synchrony-standalone.jar $E4_PROV_DIR/$E4_PROV_KEY/synchrony-standalone.jar
		fi

		rm $E4_PROV_DIR/$E4_PROV_KEY.tar.gz
}


function download_app {
	echo ">>> Attempting to download via AWS: aws s3 cp s3://e4prov/$E4_PROV_KEY.tar.gz $E4_PROV_DIR/"
	if aws s3 ls "s3://e4prov" | grep "$E4_PROV_KEY.tar.gz" > /dev/null
	then
		echo ">> Provision file found"
		mkdir -p $E4_PROV_DIR/$E4_PROV_KEY
		aws s3 cp s3://e4prov/$E4_PROV_KEY.tar.gz $E4_PROV_DIR/
		download_app_after_success
	else
		echo ">> WARN ........: Provision file not found. Starting empty."
		sleep 3
	fi
}

function download_synchrony_curl {
	download_curl_fail_if_unavailable "https://e4prov.s3.eu-central-1.amazonaws.com/synchrony-standalone.jar" "synchrony-standalone.jar"
}

function download_mysql_connector_curl {
	download_curl_fail_if_unavailable "https://e4prov.s3.eu-central-1.amazonaws.com/mysql-connector.jar" "mysql-connector.jar"
}

function download_app_curl {
	url="https://e4prov.s3.eu-central-1.amazonaws.com/$E4_PROV_KEY.tar.gz"
	echo ">>> Download via CURL: $url"
	mkdir -p "$E4_PROV_DIR/$E4_PROV_KEY"
	curl -f -o "$E4_PROV_DIR/$E4_PROV_KEY.tar.gz" "$url"
	if [[ $? -eq 0 ]]; then
		download_app_after_success
	else
		echo ">> WARN ........: Provision file not found. Starting empty."
		sleep 3
	fi
}

function kill_instance_database {
	_kill_and_remove_named_instance_if_exists ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-db
}

function kill_instance_node {
	_kill_and_remove_named_instance_if_exists ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node${1}
}

function clean_node_shared_home {
	local volume_name=${E4_APP_NAME}-shared-home-${E4_APP_VERSION_DOTFREE}
	shared_home_exists=$(docker volume ls --filter "name=${volume_name}" --format '{{.Name}}' | wc -l | awk '{print $1}')
	if (( shared_home_exists == 1 )) # arithmetic brackets ... woohoo
	then
		echo -e $C_CYN">> clean shared home ..:${C_RST}${C_GRN} Deleting${C_RST}	- Deleting existing volume ${volume_name}"
		docker volume rm --force ${volume_name}
	fi
	echo -e $C_CYN">> clean shared home ..:${C_RST}${C_GRN} Creating${C_RST}	- Creating volume ${volume_name}"
	docker volume create ${volume_name}
}

function create_network {
	local network_name=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}
	network_exists=$(docker network ls --filter "name=${network_name}" --format '{{.Name}}' | wc -l | awk '{print $1}')
	if (( network_exists == 1 )) # arithmetic brackets ... woohoo
	then
		echo -e $C_CYN">> docker network .....:${C_RST}${C_MGN} Skipping${C_RST}	- Network ${network_name} exists already."
	else
		echo -e $C_CYN">> docker network .....:${C_RST}${C_GRN} Creating${C_RST}	- Creating network ${network_name}."
		docker network create ${network_name}
	fi
}

function get_running_node_count {
	local ret_value=-1
	_is_named_container_running "${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node" ret_value

	local "$1" && return_by_reference $1 $ret_value
}

function get_running_node_name_array {
	local instance_names_string_newlines=$(docker ps --format '{{.Names}}' --filter "name=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node")
	local instance_names_string_oneline=$(echo $instance_names_string_newlines | tr "\n" " ")
	local ret_value=$instance_names_string_oneline

	local "$1" && return_by_reference $1 "$ret_value"
}

function kill_all_running_nodes {
	# NOTE: We must get all names to kill them, since e.g. node1,node4,node5 could be running
	#			 so we cannot just use a dumb counter starting from 1!
	local running_node_count=0
	get_running_node_count running_node_count
	if (( running_node_count > 0 )) # arithmetic brackets ... woohoo
	then
		echo -e $C_CYN">> docker kill nodes ..:${C_RST}${C_GRN} Killing${C_RST}	 - Killing all running ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node* instances."
		local running_instance_names=""
		get_running_node_name_array running_instance_names
		local running_instance_names_array=($running_instance_names)
		for running_instance_name in "${running_instance_names_array[@]}"
		do
			 _kill_and_remove_named_instance_if_exists ${running_instance_name}
		done
	else
		echo -e $C_CYN">> docker kill nodes ..:${C_RST}${C_MGN} Skipping${C_RST}	- No running ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node* instances present."
	fi
}

function remove_all_dangling_nodes {
	echo -e $C_CYN">> docker rm images ...:${C_RST}${C_GRN} Removing${C_RST}	- Removing dangling ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node* images."
	local dangling_ids=$(docker images | grep ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-node | awk '{ print $3 }')
	for dangling_id in $dangling_ids
	do
		docker rm $dangling_id
	done
}

function print_cluster_ready_info {
	echo -e $C_CYN">> ---------------------------------------------------------------------------------------------"$C_RST
	echo -e $C_CYN">> info ....:${C_RST}${C_GRN} Ready${C_RST} - You can now access ${E4_APP_NAME} through your browser."
	echo -e $C_CYN">> info ....:${C_RST}${C_GRN} http://${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb:${E4_LB_PUBLIC_PORT}${C_RST} "
	echo ">> Note: do not forget to add your hostname to /etc/hosts - IP_ADDRESS ${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-lb"
	echo -e $C_CYN">> ---------------------------------------------------------------------------------------------"$C_RST
}


while [[ $# > 1 ]]
do
key="$1"
case $key in
	-s|--scale)
	SCALE="$2"
	shift
	;;
	-m|--multicast)
	MULTICAST_PARAM="$2"
	shift
	;;
	-a|--action)
	ACTION="$2"
	shift
	;;
	-i|--id)
	NODE_ID="$2"
	shift
	;;
	-n|--appname)
	E4_APP_NAME="$2"
	E4_APP_NAME_UCASE="$(echo "$E4_APP_NAME" | awk '{print toupper(substr($0,0,1))tolower(substr($0,2))}')"
	shift
	;;
	-v|--version)
	E4_APP_VERSION="$2"
	# extract a dotfree number from the version string (must also work with things like '7.0.1-beta1' => 701
	E4_APP_VERSION_DOTFREE=$(echo ${E4_APP_VERSION//\./} | sed 's/-.*//g')
	E4_LB_PUBLIC_PORT=$(expr "$([[ "$E4_APP_NAME" == "jira" ]] && echo "1" || echo "2")${E4_APP_VERSION_DOTFREE}")
	if [[ "$E4_APP_NAME" = "jira" && "$E4_APP_VERSION_DOTFREE" -lt "800" ]]; then
		E4_LB_PUBLIC_PORT="60${E4_APP_VERSION_DOTFREE}"
	elif [[ "$E4_APP_NAME" = "confluence" ]]; then
		if [[ ${E4_APP_VERSION} =~ 6\.[0-9]\.[0-9] ]]; then
			echo "DEBUG: version ${E4_APP_VERSION} matches 6.x.x. Using port 50${E4_APP_VERSION_DOTFREE}"
			E4_LB_PUBLIC_PORT="50${E4_APP_VERSION_DOTFREE}"
		fi
	fi
	echo "DEBUG: $E4_APP_NAME $E4_APP_VERSION -- using port $E4_LB_PUBLIC_PORT"

	shift
	;;
	-k|--provkey)
	E4_PROV_KEY="$2"
	shift
	;;
	-h|--nodeheap)
	E4_NODE_HEAP="$2"
	shift
	;;
	*)
		 # unknown option
	;;
esac
shift
done


echo ""
echo -e $C_MGN'			__	___																________					 __					 '$C_RST
echo -e $C_MGN'		 /	|/	/___ _____	____ _____ ____		 / ____/ /_	_______/ /____	_____'$C_RST
echo -e $C_MGN'		/ /|_/ / __ `/ __ \/ __ `/ __ `/ _ \	 / /	 / / / / / ___/ __/ _ \/ ___/'$C_RST
echo -e $C_MGN'	 / /	/ / /_/ / / / / /_/ / /_/ /	__/	/ /___/ / /_/ (__	) /_/	__/ /		'$C_RST
echo -e $C_MGN'	/_/	/_/\__,_/_/ /_/\__,_/\__, /\___/	 \____/_/\__,_/____/\__/\___/_/		 '$C_RST
echo -e $C_MGN'													 /____/																						'$C_RST
echo ""
echo -e $C_MGN'	Manage local data center cluster with Docker'$C_RST
echo -e $C_MGN"	${E4_APP_NAME_UCASE} Version: ${E4_APP_VERSION}"$C_RST
echo -e $C_MGN'	------'$C_RST
echo ""

EXIT=0

if [ ! $ACTION ]
then
	echo -e $C_RED">> param error ........: Please specify action as parameter -a or --action"$C_RST
	EXIT=1
else

	if [[ ! $E4_APP_NAME ]]
	then
		echo -e $C_RED">> param error ........: Please specify version as parameter -n or --appname. E.g. --appname confluence"$C_RST
		EXIT=1
	fi

	if [[ ! $E4_APP_VERSION ]]
	then
		echo -e $C_RED">> param error ........: Please specify version as parameter -v or --version. E.g. --version 6.15.3"$C_RST
		EXIT=1
	fi

	if [[ "$ACTION" == "create" && ! $E4_PROV_KEY ]]
	then
		echo -e $C_RED">> param error ........: Please specify provisioning key -k or --provkey. E.g. --provkey conf6153_large"$C_RST
		EXIT=1
	fi

	if [[ ("$ACTION" == "create" || "$ACTION" == "update") && ! $SCALE ]]
	then
		echo -e $C_RED">> param error ........: Please specify scale as parameter -s or --scale. E.g. --scale 3"$C_RST
		EXIT=1
	fi

	if [[ ("$ACTION" == "create" || "$ACTION" == "update") && ! $E4_NODE_HEAP ]]
	then
		echo -e $C_RED">> param error ........: Please specify node heap as parameter -h or --nodeheap. E.g. --nodeheap 2048"$C_RST
		EXIT=1
	fi

	if [[ "$ACTION" == "restart-node" && ! $NODE_ID ]]
	then
		echo -e $C_RED">> param error ........: Please specify id as parameter -i or --id. E.g. --id3"$C_RST
		EXIT=1
	fi
	if [[ "$ACTION" != "info" && "$ACTION" != "restart-node" && "$ACTION" != "create" && "$ACTION" != "destroy" && "$ACTION" != "update" ]]
	then
		echo -e $C_RED">> param error ........: Please specify action as one of [ destroy, create, update, restart-node, info ]"$C_RST
		EXIT=1
	fi
fi

if [ $EXIT -eq 1 ]
then
	echo -e $C_RED">> exit ...............: quitting because of previous errors"$C_RST
	echo ""
	exit 1
fi

if [ "$ACTION" == "create" ]
then
	echo -e $C_CYN">> action .............:${C_RST}${C_GRN} CREATE${C_RST}		- Creating new cluster and destroying existing if exists"$C_RST
	echo ""

	E4_USE_AWS=false
		if [ -x "$(command -v aws)" ]; then
			if aws s3 ls "s3://e4prov" 2>&1 | grep -q 'NoSuchBucket'; then
				echo "Did not find bucket s3://e4prov. Using wget/curl for downloads."
			else
				echo "Found bucket s3://e4prov. Using aws-cli for downloads."
				E4_USE_AWS=true
			fi
		fi

	if [[ "$E4_APP_NAME" = "confluence" && ! -f $E4_PROV_DIR/synchrony-standalone.jar ]];
	then
		if [[ "$E4_USE_AWS" = true ]]; then
			download_synchrony
		else
			download_synchrony_curl
		fi
	fi

	if [[ "$E4_APP_NAME" = "jira" && ! -f $E4_PROV_DIR/mysql-connector.jar ]];
	then
		if [[ "$E4_USE_AWS" = true ]]; then
			download_mysql_connector
		else
			download_mysql_connector_curl
		fi
	fi

	if [[ ! -d $E4_PROV_DIR/$E4_PROV_KEY/${E4_APP_NAME}-home ]];
	then
		echo ">> Download provisioning set for $E4_APP_NAME_UCASE $E4_APP_VERSION with key $E4_PROV_KEY"
		if [[ "$E4_USE_AWS" = true ]]; then
			download_app $E4_PROV_KEY
		else
			download_app_curl $E4_PROV_KEY
		fi

	else
		echo ">> Provision resources found for $E4_APP_NAME_UCASE $E4_APP_VERSION with key $E4_PROV_KEY"
	fi

	if [[ -d $E4_PROV_DIR/$E4_PROV_KEY ]];
	then
		chmod -R 777 $E4_PROV_DIR/$E4_PROV_KEY # FIXME: sometimes we run into file permission issues and I haven't found a way to fix it yet
	fi

	create_network
	echo ""

	kill_all_running_nodes
	echo ""

	remove_all_dangling_nodes
	echo ""

	clean_node_shared_home
	echo ""

	kill_instance_database
	if [[ "$E4_APP_NAME" = "jira" ]];
	then
		start_instance_database_mysql
	else
		start_instance_database
	fi

	echo ""

	kill_instance_loadbalancer
	start_instance_loadbalancer $SCALE
	echo ""

	echo ">>> Wait for database init to complete"
	wait_for_logs_to_contain "db" "E4_DB_INIT_DONE"
	sleep 5

	for (( node_id=1; node_id<=$SCALE; node_id++ ))
	do
		kill_instance_node $node_id
		start_instance_node $node_id
		if [[ "${node_id}" = "1" ]];
		then
			echo ">>> Wait for node 1 to be fully started"
			wait_for_logs_to_contain "node1" "Server startup in"
		fi
	echo ""
	done
	echo ""

	if [[ "$SCALE" != "1" ]];
	then
		echo ">>> Wait for last node (node $SCALE) to be fully started"
		wait_for_logs_to_contain "node$SCALE" "Server startup in"
	fi

	print_cluster_ready_info
	echo ""
fi

if [ "$ACTION" == "destroy" ]
then
	echo -e $C_CYN">> action .............:${C_RST}${C_GRN} DESTROY${C_RST}	 - Shutting down cluster and destroying instances."$C_RST
	echo ""

	kill_all_running_nodes
	echo ""

	kill_instance_database
	echo ""

	kill_instance_loadbalancer
	echo ""
fi

if [ "$ACTION" == "update" ]
then
	echo -e $C_CYN">> action .............:${C_RST}${C_GRN} UPDATE${C_RST}		- Update running cluster."$C_RST
	echo ""

	running_node_count=0
	get_running_node_count running_node_count
	if (( running_node_count > 0 )) # arithmetic brackets ... woohoo
	then
		echo -e $C_CYN">> update .............:${C_RST}${C_GRN} OK${C_RST}				- currently ${running_node_count} ${E4_APP_NAME_UCASE} nodes are running. Cluster should be scaled to ${SCALE} ${E4_APP_NAME_UCASE} nodes."$C_RST
		start_node_id=$(($running_node_count + 1))
		for (( node_id=$start_node_id; node_id<=$SCALE; node_id++ ))
		do
			kill_instance_node $node_id
			start_instance_node $node_id
			echo ""
		done
		echo ""

		kill_instance_loadbalancer
		sleep 2
		start_instance_loadbalancer $SCALE
		echo ""
	else
		echo -e $C_CYN">> update .............:${C_RST}${C_RED} FAIL${C_RST}			- currently 0 ${E4_APP_NAME_UCASE} nodes are running. Try to create the cluster first."$C_RST
	fi
	echo ""
fi

if [ "$ACTION" == "restart-node" ]
then
	echo -e $C_CYN">> action .............:${C_RST}${C_GRN} RESTART-N${C_RST} - Restarting ${E4_APP_NAME_UCASE} node ${NODE_ID}."$C_RST
	echo ""

	kill_instance_node $NODE_ID
	start_instance_node $NODE_ID
	echo ""
fi

if [ "$ACTION" == "info" ]
then
	echo -e $C_CYN">> action .............:${C_RST}${C_GRN} INFO${C_RST}			- Cluster information."$C_RST
	echo ""

	running_node_count=0
	get_running_node_count running_node_count
	echo -e $C_CYN">> info ...............:${C_RST}${C_GRN} OK${C_RST}				- currently ${running_node_count} ${E4_APP_NAME_UCASE} node(s) are running. Showing 'docker ps' for cluster:"$C_RST
	echo ""
	docker ps --format '{{.ID}}\t {{.Names}}\t {{.Ports}}' --filter "name=${E4_APP_NAME}-cluster-${E4_APP_VERSION_DOTFREE}-*"
	echo ""
fi
