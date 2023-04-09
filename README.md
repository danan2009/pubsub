# Pubsub Project Introduce #

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