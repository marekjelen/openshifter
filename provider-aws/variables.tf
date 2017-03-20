//  AWS region to deploy our cluster
variable "region" {
  description = "Region to deploy the cluster into"
  //default = "us-west-1"
  default = "eu-west-1"
}

// AWS zone to deploy our cluster
variable "zone" {
  description = "Zone to deploy the cluster into"
  default = "us-west-1a"
}

// Machine type
variable "machine" {
  description = "Machine type to use for the cluster"
  //default = "m3.medium"
  default = "t2.large"
}

// SSH key name
variable "key_name" {
  description = "The name of the key to user for ssh access, e.g: os-cluster"
  default = "os-cluster"
}

// Public key to use for SSH access.
variable "public_key_path" {
  description = "The local path to the public SSH key path, e.g. ~/.ssh/id_rsa.pub"
  default = "~/.ssh/aws.pub"
}
