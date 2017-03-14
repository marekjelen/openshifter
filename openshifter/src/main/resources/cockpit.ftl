{
   "kind": "Template",
   "apiVersion": "v1",
   "metadata": {
      "name": "openshift-cockpit"
   },
   "labels": {
      "createdBy": "openshift-cockpit-template"
   },
   "parameters": [
      {
         "description": "The public url for the Openshift OAuth Provider",
         "name": "OPENSHIFT_OAUTH_PROVIDER_URL",
         "value": "https://console.${deployment.name}.${deployment.domain}:8443",
         "required": true
      },
      {
         "description": "The public url for the Openshift OAuth Provider",
         "name": "COCKPIT_KUBE_URL",
         "value": "cockpit.apps.${deployment.name}.${deployment.domain}",
         "required": true
      },
      {
         "description": "The public url for the Openshift OAuth Provider",
         "name": "COCKPIT_KUBE_INSECURE",
         "value": "true",
         "required": false
      },
      {
         "description": "Oauth client secret",
         "name": "OPENSHIFT_OAUTH_CLIENT_SECRET",
         "from": "user[a-zA-Z0-9]{64}",
         "generate": "expression"
      },
      {
         "description": "Oauth client id",
         "name": "OPENSHIFT_OAUTH_CLIENT_ID",
         "value": "cockpit-openshifter-oauth-client"
      },
      {
         "description": "Skip kubernetes CA verification",
         "name": "KUBERNETES_INSECURE",
         "value": ""
      },
      {
         "description": "PEM Encoded certificate to use for CA verification",
         "name": "KUBERNETES_CA_DATA",
         "value": ""
      },
      {
         "description": "Host name that the registry is accessible at",
         "name": "REGISTRY_HOST",
         "value": ""
      },
      {
         "description": "Show only the registry interface",
         "name": "REGISTRY_ONLY",
         "value": "false"
      }
   ],
<#noparse>
   "objects": [
      {
         "kind": "DeploymentConfig",
         "apiVersion": "v1",
         "metadata": {
            "name": "openshift-cockpit",
            "labels": {
               "name": "openshift-cockpit"
            }
         },
         "spec": {
            "replicas": 1,
            "selector": {
               "name": "openshift-cockpit"
            },
            "template": {
               "metadata": {
                  "labels": {
                     "name": "openshift-cockpit"
                  }
               },
               "spec": {
                  "containers": [
                     {
                        "name": "openshift-cockpit",
                        "image": "cockpit/kubernetes",
                        "ports": [
                           {
                              "containerPort": 9090,
                              "protocol": "TCP"
                           }
                        ],
                        "env": [
                           {
                              "name": "OPENSHIFT_OAUTH_PROVIDER_URL",
                              "value": "${OPENSHIFT_OAUTH_PROVIDER_URL}"
                           },
                           {
                              "name": "OPENSHIFT_OAUTH_CLIENT_ID",
                              "value": "${OPENSHIFT_OAUTH_CLIENT_ID}"
                           },
                           {
                              "name": "KUBERNETES_INSECURE",
                              "value": "${KUBERNETES_INSECURE}"
                           },
                           {
                              "name": "KUBERNETES_CA_DATA",
                              "value": "${KUBERNETES_CA_DATA}"
                           },
                           {
                              "name": "COCKPIT_KUBE_INSECURE",
                              "value": "${COCKPIT_KUBE_INSECURE}"
                           },
                           {
                              "name": "REGISTRY_HOST",
                              "value": "${REGISTRY_HOST}"
                           },
                           {
                              "name": "REGISTRY_ONLY",
                              "value": "${REGISTRY_ONLY}"
                           }
                        ]
                     }
                  ]
               }
            }
         }
      },
      {
         "kind": "Service",
         "apiVersion": "v1",
         "metadata": {
            "name": "openshift-cockpit",
            "labels": {
               "name": "openshift-cockpit"
            }
         },
         "spec": {
            "type": "ClusterIP",
            "ports": [
               {
                  "name": "legacy",
                  "protocol": "TCP",
                  "port": 9000,
                  "targetPort": 9090
               },
               {
                  "name": "http",
                  "protocol": "TCP",
                  "port": 80,
                  "targetPort": 9090
               },
               {
                  "name": "https",
                  "protocol": "TCP",
                  "port": 443,
                  "targetPort": 9090
               }
            ],
            "selector": {
               "name": "openshift-cockpit"
            }
         }
      },
      {
          "kind": "Route",
          "apiVersion": "v1",
          "metadata": {
              "name": "openshift-cockpit",
              "labels": {
                  "name": "openshift-cockpit"
              }
          },
          "spec": {
              "host": "${COCKPIT_KUBE_URL}",
              "to": {
                  "kind": "Service",
                  "name": "openshift-cockpit",
                  "weight": 100
              },
              "port": {
                  "targetPort": "legacy"
              }
          }
      },
      {
         "kind": "OAuthClient",
         "apiVersion": "v1",
         "metadata": {
            "name": "${OPENSHIFT_OAUTH_CLIENT_ID}"
         },
         "respondWithChallenges": false,
         "secret": "${OPENSHIFT_OAUTH_CLIENT_SECRET}",
         "allowAnyScope": true,
         "redirectURIs": [
            "http://${COCKPIT_KUBE_URL}"
         ]
      }
   ]
}
</#noparse>
