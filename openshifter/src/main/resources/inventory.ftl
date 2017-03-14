[OSEv3:children]
masters
nodes

[masters]
${cluster.instance("master").address}<#if cluster.instance("master") == cluster.instance("infra")> openshift_schedulable=true</#if>

[nodes]
<#list cluster.instances("node") as instance>
${instance.address} openshift_schedulable=true openshift_node_labels="{'region': '<#if instance == cluster.instance("infra")>infra<#else>primary</#if>', 'zone': 'default'}"
</#list>

[OSEv3:vars]
ansible_user=root

containerized=True

<#if deployment.type == 'origin'>
deployment_type=origin
openshift_release=${deployment.release}
</#if>

<#if deployment.type == 'ocp'>
deployment_type=openshift-enterprise
</#if>

<#if deployment.components.metrics??>
openshift_hosted_metrics_deploy=True
</#if>

<#if deployment.components.logging??>
openshift_hosted_logging_deploy=True
</#if>

openshift_master_identity_providers=[{'name': 'htpasswd_auth', 'login': 'true', 'challenge': 'true', 'kind': 'HTPasswdPasswordIdentityProvider', 'filename': '/etc/origin/master/htpasswd'}]

openshift_public_hostname=console.${deployment.name}.${deployment.dns.suffix}
openshift_master_default_subdomain=apps.${deployment.name}.${deployment.dns.suffix}