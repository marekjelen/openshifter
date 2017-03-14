# OpenShifter
Tool to provision and bootstrap an OpenShift cluster in the cloud.

## How to run OpenShifter

1. Create working directory, e.g. ~/deployer.

1. Add into this directory a private key that will be used for connecting to the server with the name `private.key`.

   > This key can be any key you want to use - it is not tied to any specific Google account. Can be your personal key or one that you just create for using this tool

1. Create the definition file, e.g. `cluster01.yml` in the same directory.
The file name will be the prefix of the DNS name for your cluster.

   > In the definition file you'll need to add the public part of the key you added before, so that the tool can connect.

1. Execute openshifter

   ```
   docker run -ti -v ~/deployer:/root/data docker.io/mjelen/openshifter create/destroy/cleanup cluster01
   ```

   > To be able to pull down the openshifter image from gitlab you need to be in the osevg organization, and you'll be required to execute __docker login registry.gitlab.com__ with your gitlab credentials.

## Definition file

Sample definition file might look like

```yaml
name: <base file name>        # optional, override the name of the cluster
provider: <provider id>       # required, your provider of choice
type: origin                  # optional, OpenShift version - origin or ocp
release: v1.4.0               # optional, OpenShift version to install, https://hub.docker.com/r/openshift/origin/tags/
installer: ansible            # optional, installer to use to install Openshift - ansible, ocu

dns:
  zone: <zone name>           # optional, zone name to configure
  suffix: nip.io              # optional, domain name suffix

ssh:
  keys:
    - <ssh public key>        # required, public part of the key pair without comment

nodes:
  zone: <zone to use>         # required, zone to use
  region: <region to use>     # optional, region to use, if not set, is calculated or not required by provider
  type: <node type>           # optional, node type, specific per provider, e.g. n1-standard-1 in GCE
  
  count: 0                    # optional, 0 nodes and infra set to false is all in one deployment
  infra: false                # optional, should be infra node split from master
  
  disks:                      # optional, disk configuration, by default root and docker disks 
    - size: 100               # optional, number of GB
      boot: true              # optional, is the disk boot device, has to be first if true
      type: ssd               # optional, disk type, ssd or hdd
    - size: 100               
      boot: false              
      type: ssd               

  nodes:                      # optional, configure nodes per type
    infra:                    # optional, configure infra nodes
      disks:                  # optional, by default infra node gets one more disk when PVs are enabled
        - size: 100
          boot: false
          type: ssd

components:                   # optional, components to setup on the cluster
  pvs: false                  # optional, PersistentVolumes
  nodePorts: false            # optional, allow NodePorts
  metrics: false              # optional, Metrics
  cockpit: false              # optional, Cockpit
  logging: false              # optional, Logging
  hostPath: false             # optional, allow HostPath
  runAsRoot: false            # optional, allow running root containers

users:                        # optional, user accounts to setup on the cluster
  admin:                      # optional, omnipotent admin account
    username: <name>          # required, user name
    password: <pass>          # required, user password

  regular:                    # optional, regular users to create
    - username: <name>        # required, user name
      password: <pass>        # required, user password
      sudoer: false           # optional, allow the used to sudo

  random:                     # optional, random users for workshops user<min> to user<max>
    username: user            # required, username prefix (e.g. user01)
    password: password        # required, password prefix (e.g. password01)
    min: 0                    # optional, lower bound of the range
    max: 0                    # optional, upper bound of the range
    execute:                  # optional, execute these command in user's project using oc cli
      - new-app php~https://github.com/gshipley/smoke.git
      - expose service smoke

templates:                    # optional, templates to import into the openshift project
  - https://gitlab.com/gitlab-org/omnibus-gitlab/raw/master/docker/openshift-template.json

execute:                      # optional, execute these command using the oc cli
  - get all -n default

docker:                       # optional, configure Docker
  prime:                      # optional, pre-fetch images on each node
    - mjelen/example

pvs:                          # required if PersistentVolumes components is enabled
  type: host                  # optional, PersistentVolume technology to use
  count: 0                    # optional, how many PersistentVolumes to create
  size: 5                     # required, PersistentVolume size in GB
```

## Providers

### Google Compute Engine [gce]

Currently most stable and useful for normal installation and for `oc cluster up`.

[Create new ServiceAccount](https://console.cloud.google.com/iam-admin/serviceaccounts) and assign permission

* Project -> Owner

finally download the credentials as JSON file and save them to your working directory.

Set up your DNS

* Open the Google Cloud Console and go to Networking -> [Cloud DNS](https://console.cloud.google.com/networking/dns/zones)
* Register new zone for your domain
* Configure your domain with at your registrar as described under "Registrar setup" (link in top right corner)

Update your definition file

```yaml
provider: gce                 # Use GCE as provider

dns:
  zone: <zone name>           # optional, zone name to use in Cloud DNS 

nodes:
  type: n1-standard-1         # optional, machine type to use
  zone: us-west1-a            # optional, zone to use
  region: us-west1            # optional, region to use, if not set, is calculated by stripping last segment from zone

gce:                          # required, configure GCE provider
  project: <project id>       # required, ID of the project to use
  account: <the JSON file>    # required, name of the JSON file with account credentials 
```

### Linode [linode]

Mostly useful for quick `oc cluster up` installations.

Update your definition file

```yaml
provider: linode              # required, Use Linode as provider

nodes:
  type: 8192                  # optional, Memory size (2048, 4096, 8192, 12288, 24576, 49152, 65536, 81920, 122880)
  region: dallas              # optional, Datacenter (dallas, fremont, atlanta, newark, london, tokyo, singapore, frankfurt, shinagawa1)
  
linode:                       # required, Configure Linode provider
  key: <linode key>           # required, Linode API kay                 
```

## Installers
OpenShifter provides different ways to install your cluster.

### Ansible [ansible]
When this installer is configured, it will use __ansible__ to do the installation of the cluster. This is required for multi nodes cluster.

### oc cluster up [ocu]
When this installer is configured, it will use __oc cluster up__ to do the installation of the cluster. This cluster will be a one node all-in-one cluster. This installer is much faster than ansible, but provides a limited set of functionalities, so it's a good option for test clusters.


## Troubleshooting

* If when you do the provisioning of the cluster, you get this error:

  ```
  com.google.api.client.auth.oauth2.TokenResponseException: 400 Bad Request
  {
    "error" : "invalid_grant",
    "error_description" : "Invalid JWT: Token must be a short-lived token and in a reasonable timeframe"
  }
  ```

  The problem might be related to an out-of-time-synch docker daemon. (This happens quite often in macOS).
  Just restart the daemon.

* If you get an error about not finding the Main class when running on Linux:

 ```
 Error: Could not find or load main class eu.mjelen.openshifter.cli.Main
 ```

 Then you should add the :Z flag to your volumes to set the correct SELinux:

 ```
 docker run -ti -v ~/deployer:/root/data:Z registry.gitlab.com/osevg/openshifter create cluster01.yml
 ```
