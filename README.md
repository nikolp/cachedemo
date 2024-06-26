## Caching exploration with REDIS including CloudFormation and Terraform

### References
https://reflectoring.io/spring-cloud-aws-redis/

https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-caching.html

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.cache.spring.cache.cache-names

https://www.baeldung.com/spring-boot-evict-cache

https://www.baeldung.com/spring-setting-ttl-value-cache


### Local testing in IntelliJ
* One time thing, colima needed to run docker 
```colima start```
* Start redis via docker
```docker run --name my-redis -p 6379:6379 -d redis```

* Your **CacheDemoApplication** "Edit Config" should 
  * Have extra command line arg **--spring.profiles.active=local**
  * Or give environment 
  variable **SPRING_PROFILES_ACTIVE=local**
* Run it and then hit http://localhost:8080/book/10
* You can also run/debug the unit tests (but mvn package below will also run them)
* You can `docker stop my-redis` to confirm error handling as the app should log caching errors but not crash


### Build and test 
```
# one time thing
colima start
docker network create mynetwork
# start redis
docker run --name my-redis -p 6379:6379 -d --network mynetwork redis
# The command below fails as of Jun 2024.
# Unit test use embedded redis and the current version of embedded redis is broken.
# Option: point to a fork of embedded redis that works, see https://stackoverflow.com/a/77877991
# Option: comment out this line in the test: @Import(TestConfigurationEmbeddedRedis.class)
# in which case tests will rely on redis started above via docker (not ideal as no proper isolation)
# Option: skip the tests via mvn clean package -DskipTests
mvn clean package
sudo docker buildx build -t cachedemo:latest .
# playing with port 5000 just for kicks to see port forwarding working
docker run -e SPRING_PROFILES_ACTIVE=localdocker -p 5000:8080 --network mynetwork cachedemo:latest
# confirm that it works by hitting http://localhost:5000/book/10
# then build it for deployment on Amazon EC2 typical servers which are amd64 not arm64 like my local Mac
docker buildx build --platform=linux/amd64 -t cachedemo:latest .
docker tag cachedemo:latest philip11/cachedemo:latest
# the login below not needed often
docker login --username=philip11
docker push philip11/cachedemo:latest
```

### Launch EC2
* Navigate to [EC2](https://us-east-1.console.aws.amazon.com/ec2/home?region=us-east-1) 
* Launch Instance
* Name: Webserver
* Image: Amazon Linux (free tier)
* Arch: 64-bit (x86)
* Type: t2.micro (free tier eligible)
* Keypair: MyBeanstalkEc2
* Select "existing security group" = allow-all
  (It allows all ssh, http and https traffic)
* "Launch Instance"

### Launch Instance From Template
All of the above settings (and some of the install commands below)
have been added to a launch template for my own ease of use.

### Deploy and Run on EC2
```
# Use Instance Connect or the ssh command given by EC2
sudo yum update -y
sudo yum install docker -y
sudo service docker start

# Option 1 Local: start up the cache
sudo docker network create mynetwork
sudo docker run --name my-redis -p 6379:6379 -d --network mynetwork redis
# start up app that uses it
sudo docker run -d -p 80:8080 -e SPRING_PROFILES_ACTIVE=localdocker --network mynetwork --name cachedemo philip11/cachedemo:latest

# Option 2 Elasticache: start up the Redis cluster in AWS Console (see Readme)
# In a few minutes, note the "primary endpoint"
# start up app that uses it
sudo docker run -d -p 80:8080 -e REDIS_HOST=<primary_endpoint_without_port> --name cachedemo philip11/cachedemo:latest

sudo docker ps
sudo docker logs cachedemo
# MUST use HTTP (not S). MUST use port 80.
curl http://0.0.0.0:80/book/3
curl http://0.0.0.0/book/3
# Or get Pubic IPv4 and try same thing externally, like e.g.
curl http://3.80.205.78/book/50
```

### Deploy and Run on EC2 with CloudFormation
See ```cloud-form.yml``` and read the file comments on how to use it.
To build a more interesting tech stack redis and app are running on different ec2 instances.

### Deploy and Run on EC2 with Terraform
See sample commands below
```
cd terraform
terraform init
terraform apply
# above command will output a sample URL that you can hit
# finally remember to shut down everything
terraform destroy -auto-approve
``` 

### More on local redis
As an alternative to docker
```
brew install redis
```
This will remind you
```
brew services start redis
Or, if you don't want/need a background service you can just run:
/opt/homebrew/opt/redis/bin/redis-server /opt/homebrew/etc/redis.conf
```
But for now just using this to run
```
redis-cli
```

### ElastiCache Startup
* Create Redis Cluster
* Configure and create new cluster (instead of the "easy" option)
* Cluster mode: disabled
* Name: my-redis-cluster
* Node type: cache.t2.micro
* Number of replicas: 1 (for example, could even be 0)
* Subnet group: choose existing (was previously created)
* AZ placement: no preference
* Security Group: select "manage" and pick "redis" (allows inbound connection)
* Backup: turn off
