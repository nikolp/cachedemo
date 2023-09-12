## Caching exploration with REDIS

### References
https://www.baeldung.com/spring-boot-evict-cache

https://www.baeldung.com/spring-setting-ttl-value-cache

### Build and test 
```
docker buildx build -t cachedemo:latest .
docker run -p 5000:8080 cachedemo:latest
# confirm that it works, then build it for deployment
docker buildx build --platform=linux/amd64 -t cachedemo:latest .
docker tag cachedemo:latest philip11/cachedemo:latest
# the login below not needed often
docker login --username=philip11
docker push philip11/cachedemo:latest
```

### Launch EC2
* Navigate to EC2 in aws console
* Launch Instance
* Name: Webserver
* Image: Amazon Linux (free tier)
* Arch: 64-bit (x86)
* Type: t2.micro (free tier eligible)
* Keypair: MyBeanstalkEc2
* Select "existing security group" = launch-wizard-1
  (It allows all ssh, http and https traffic)
* "Launch Instance"

### Deploy and Run on EC2
```
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo docker run -d -p 80:8080 --name cachedemo philip11/cachedemo:latest
sudo docker ps
sudo docker logs cachedemo
# MUST use HTTP (not S). MUST use port 80.
curl http://0.0.0.0:80/book/3
curl http://0.0.0.0/book/3
# Or get Pubic IPv4 and try same thing externally, like e.g.
curl http://3.80.205.78/book/50
```