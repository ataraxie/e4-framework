#!/bin/bash

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

sudo usermod -a -G docker ubuntu

mkdir /home/ubuntu/e4prov

echo "Writing to .bashrc: 'chmod -R 777 /home/ubuntu/e4prov'"

echo "export E4_PROV_DIR=/home/ubuntu/e4prov" >> /home/ubuntu/.bashrc

echo "Setting pulic access to e4prov dir"
chmod -R 777 /home/ubuntu/e4prov

echo "Done. Note that you must re-login to your terminal. If you want to use AWS, run 'sudo apt-get install awscli && aws configure'."

#sudo apt-get install awscli
#aws configure