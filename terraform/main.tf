terraform {
  # what version of terraform to use
  required_version = ">= 1.0"
  required_providers {
    # the preferred "local name" for the aws provider
    aws = {
      source  = "hashicorp/aws"
      # What version to use. For illustration, pin it to something that surely works.
      # .terraform.lock.hcl will store exactly what got downloaded so that others can get
      # reproducible results. If you change the constraints below and expect an update be sure to run
      # "tf init -upgrade" instead of just "tf init"
      # otherwise "tf init" will simply download the exact thing from .terraform.lock.hcl
      # even if you version is very permissible like ">= 5.0.0"
      version = "5.54.1"
    }
  }
}

# Additional config for aws provider (beyond the version which was specified above)
# Tf knows how to get login credentials from the same place as "aws configure"
# and since that was done on this computer, no need to specify them here.
provider "aws" {
  region = "us-east-1"
}

# i know the name of the group i want to use but don't want to hardcode its id
data "aws_security_group" "allow-all-security-group" {
  name = "allow-all"
}

data "aws_security_group" "redis-security-group" {
  name = "redis"
}

locals {
  user_data_redis = <<-EOT
          #!/bin/bash -xe
          yum update -y
          yum install docker -y
          service docker start
          docker run -d --name my-redis -p 6379:6379 redis
  EOT
}

module "ec2-instance-redis" {
  source  = "terraform-aws-modules/ec2-instance/aws"
  version = "5.6.1"
  name = "redis"
  ami_ssm_parameter = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2"
  instance_type          = "t2.micro"
  key_name               = "MyBeanstalkEc2"
  associate_public_ip_address = true
  # "allow-all" group is slightly misnamed, it does not open up redis port
  # so have to explicitly add the "redis" security group which handles that piece
  vpc_security_group_ids = [
    data.aws_security_group.allow-all-security-group.id,
    data.aws_security_group.redis-security-group.id,
  ]
  user_data_base64 = base64encode(local.user_data_redis)
  # trigger a take-down and re-create of the EC2 instance of the user_data has changed
  user_data_replace_on_change = true
}

# notice this has implicit dependency on output of redis module
locals {
  user_data_app = <<-EOT
            #!/bin/bash -xe
            yum update -y
            yum install docker -y
            service docker start
            docker run -d -p 80:8080 -e REDIS_HOST=${module.ec2-instance-redis.public_dns} --name cachedemo philip11/cachedemo:latest
  EOT
}

module "ec2-instance-app" {
  source  = "terraform-aws-modules/ec2-instance/aws"
  version = "5.6.1"
  name = "app"
  ami_ssm_parameter = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2"
  instance_type          = "t2.micro"
  key_name               = "MyBeanstalkEc2"
  associate_public_ip_address = true
  vpc_security_group_ids = [data.aws_security_group.allow-all-security-group.id]
  user_data_base64 = base64encode(local.user_data_app)
  # trigger a take-down and re-create of the EC2 instance of the user_data has changed
  user_data_replace_on_change = true
}

output "example-app-call" {
  value = "curl http://${module.ec2-instance-app.public_ip}/book/15"
}


