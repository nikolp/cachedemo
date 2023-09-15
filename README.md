## Caching exploration with REDIS

### References
https://reflectoring.io/spring-cloud-aws-redis/

https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-caching.html

https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.cache.spring.cache.cache-names

https://www.baeldung.com/spring-boot-evict-cache

https://www.baeldung.com/spring-setting-ttl-value-cache


### Local testing in IntelliJ
* Your **CacheDemoApplication** "Edit Config" should 
  * Have extra command line arg **--spring.profiles.active=local**
  * Or give environment 
  variable **SPRING_PROFILES_ACTIVE=local**
* Run it and then hit http://localhost:8080/book/10
* You can also run/debug the unit tests (but mvn package below will also run them)


### Build and test 
```
# one time thing
docker network create mynetwork
# start redis
docker run --name my-redis -p 6379:6379 -d --network mynetwork redis
mvn clean package
docker buildx build -t cachedemo:latest .
# playing with port 5000 just for kicks to see port forwarding working
docker run -e SPRING_PROFILES_ACTIVE=localdocker -p 5000:8080 --network mynetwork cachedemo:latest
# confirm that it works by hitting http://localhost:5000/book/10
# then build it for deployment
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

# Option 2 Elasticache: start up the Redis cluster in AWS Console
# In a few minutes, note the "primeary endpoint"
# start up app that uses it
sudo docker run -d -p 80:8080 -e REDIS_HOST=<primary_endpoint_without_port> --network mynetwork --name cachedemo philip11/cachedemo:latest


sudo docker ps
sudo docker logs cachedemo
# MUST use HTTP (not S). MUST use port 80.
curl http://0.0.0.0:80/book/3
curl http://0.0.0.0/book/3
# Or get Pubic IPv4 and try same thing externally, like e.g.
curl http://3.80.205.78/book/50
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