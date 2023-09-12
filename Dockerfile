# Adapted from https://towardsaws.com/deploy-spring-boot-application-to-aws-ec2-using-docker-f359e7ad2026
# docker buildx build -t cachedemo:latest .
# docker run -p 5000:8080 cachedemo:latest
# confirm that it works, then build it for deployment
# docker buildx build --platform=linux/amd64 -t cachedemo:latest .
# docker tag cachedemo:latest philip11/cachedemo:latest
# docker login --username=philip11
# docker push philip11/cachedemo:latest

# EC2 console
# Launch Instance
# Name: Webserver
# Image: Amazon Linux (free tier)
# Arch: 64-bit (x86)
# Type: t2.micro (free tier)
# Keypair: MyBeanstalkEc2
# Select "existing security group" = launch-wizard-1
# It allows all ssh, http and https traffic
# "Launch Instance"

# on ec2
# sudo yum update -y
# sudo yum install docker -y
# sudo service docker start
# sudo docker run -d -p 80:8080 --name cachedemo philip11/cachedemo:latest
# sudo docker ps
# sudo docker logs cachedemo
# MUST use HTTP (not S)
# curl http://0.0.0.0:80/book/3
# curl http://0.0.0.0/book/3
# Or get Pubic IPv4 and try same thing externally, like e.g.
# curl http://3.80.205.78/book/50

FROM openjdk:17-jdk

WORKDIR /app

COPY target/cachedemo-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
