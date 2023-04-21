# Pubsub Project Introduce #

### Author:
JamesXWZ

Generate this project via maven:

``
mvn archetype:generate -DgroupId=top.sawied.hs -DartifactId=pubsub -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
`` 

Start Docker 
```
sudo service docker start
sudo service --status-all
```
Configure Docker to start on boot with systemd

```
sudo systemctl enable docker.service
sudo systemctl enable containerd.service
```

To stop this behavior, use disable instead.
```
 sudo systemctl disable docker.service
 sudo systemctl disable containerd.service
```

```
 sudo docker-compose up -d
```

```
IBM MQ Image:
docker pull icr.io/ibm-messaging/mq:9.3.0.4-r2
```

### IBM Account.2015
danan.2009@hotmail.com




### Docker installation 

```shell

sudo apt-get remove docker docker-engine docker.io containerd runc

sudo apt-get update
sudo apt-get install \
    ca-certificates \
    curl \
    gnupg

sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg


echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
  
sudo apt-get update
  
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

```
