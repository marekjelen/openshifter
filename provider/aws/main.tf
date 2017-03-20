// Based on https://github.com/dwmkerr/terraform-aws-openshift

//  Setup the core provider information.
provider "aws" {
  region  = "${var.region}"
}


//  Define the RHEL 7.2 AMI by:
//  RedHat, Latest, x86_64, EBS, HVM, RHEL 7.2
data "aws_ami" "rhel7_2" {
  most_recent = true

  owners = ["309956199498"] // Red Hat's account ID.

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "name"
    values = ["RHEL-7.2*"]
  }
}

resource "aws_instance" "node" {
  ami                  = "${data.aws_ami.rhel7_2.id}"
  instance_type        = "${var.machine}"

  tags {
    Name    = "OpenShift Node"
    Project = "openshift"
  }
}
