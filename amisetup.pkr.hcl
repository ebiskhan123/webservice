packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.1"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "source_ami" {
  default = ""
}

variable "ami_region" {
  default = ""
}

variable "ami_name" {
  default = ""
}

variable "ssh_username" {
  default ="ec2-user"
}

variable "AWS_ACCESSKEY" {
  default =""
}

variable "AWS_SECTRET_KEY" {
  default =""
}

variable "instance_type" {
  default = "t2.micro"
}

source "amazon-ebs" "ec2-user" {
  access_key      = "${var.AWS_ACCESSKEY}"
  secret_key      = "${var.AWS_SECTRET_KEY}"
  region          = "${var.ami_region}"
  instance_type   = "${var.instance_type}"
  source_ami      = "${var.source_ami}"
  ssh_username    = "${var.ssh_username}" 
  ami_name        = "${var.ami_name}"
  ami_description = "AMI with Java, Maven and MySql"
  ami_users       = [605680160689]
  // launch_block_device_mappings {
  //   device_name = "/dev/sda1"
  //   volume_size = 40
  //   volume_type = "gp2"
  //   delete_on_termination = true   amisetup.auto.pkrvars.hcl
  // }
}

build {
  sources = ["source.amazon-ebs.ec2-user"]

  provisioner "shell" {
    scripts = [
      "./buildscript.sh",
    ]
  }
  provisioner "shell" {
    scripts = [
      "./buildscript.sh",
    ]
  }
  provisioner "shell" {
    scripts = [
      "./buildscript.sh",
    ]
  }
}