//  AWS region to deploy our cluster
variable "region" {
  description = "Region to deploy the cluster into"
  //default = "us-west-1"
  default = "eu-west-1"
}

//  AWS zone to deploy our cluster
variable "zone" {
  description = "Zone to deploy the cluster into"
  default = "us-west-1a"
}

// Machine type
variable "machine" {
  description = "Machine type to use for the cluster"
  default = "m3.medium"
}

//  The public key to use for SSH access.
variable "public_key_path" {
  default = "~/.ssh/aws.pub"
}
