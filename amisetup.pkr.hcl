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
  default = "ec2-user"
}

variable "AWS_ACCESSKEY" {
  default = ""
}

variable "AWS_SECTRET_KEY" {
  default = ""
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
  ami_users       = ["037023148215","556795868226"]
}

build {
  sources = ["source.amazon-ebs.ec2-user"]

  provisioner "shell" {
    inline = ["sleep 10"]
  }
  provisioner "file" {
    source      = "webservice.zip"
    destination = "~/"
  }
  provisioner "shell" {
    inline = [
      "cd ~",
      "sudo mkdir -v -m755 webservice",
      "sudo unzip webservice.zip -d webservice",
      "cd webservice",
      "echo $(ls)"
    ]
  }
  provisioner "shell" {
    scripts = [
      "./presetup.sh",
    ]
  }
  provisioner "shell" {
    scripts = [
      "./postsetup.sh",
    ]
  }
  provisioner "shell" {
    environment_vars = ["CURRENTREGION=${var.ami_region}"]
    scripts = [
      "./codedeploy.sh",
    ]
  }
  provisioner "shell" {
      scripts = [
      "./cloudwatch.sh",
    ]
  }
}