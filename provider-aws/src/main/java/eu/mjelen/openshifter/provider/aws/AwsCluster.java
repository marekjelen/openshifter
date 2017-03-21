package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;

import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsCluster implements Cluster {

    private final Deployment deployment;
    private final Terraformer tf;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AwsCluster(Deployment deployment) {
        this.deployment = deployment;
        this.tf = new Terraformer(deployment);
    }

    @Override
    public Instance instance(String label) {
        return null;
    }

    @Override
    public List<? extends Instance> instances() {
        return null;
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return null;
    }

    @Override
    public void validate() {
      if(!this.tf.initTemplates()) {
        System.exit(1);
      }
      this.logger.info("terraform plan");
      this.tf.execTemplate("plan");
    }

    @Override
    public void create() {
      this.logger.info("terraform apply");
      this.tf.execTemplate("apply");
    }

    @Override
    public void destroy() {
      this.logger.info("terraform destroy");
      this.tf.execTemplate("destroy");
    }

}
