package eu.mjelen.openshifter.provider.gce;

import com.google.api.services.deploymentmanager.model.ConfigFile;
import com.google.api.services.deploymentmanager.model.Operation;
import com.google.api.services.deploymentmanager.model.TargetConfiguration;
import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.provider.gce.template.GceTemplate;
import eu.mjelen.warden.templates.Templates;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GceCluster2 implements Cluster {

    private final Deployment deployment;
    private final GceTemplate template;
    private final Templates templates = new Templates() {};

    private GceClient client;

    public GceCluster2(Deployment deployment) {
        this.deployment = deployment;
        this.template = new GceTemplate(this.deployment);
        try {
            this.client = new GceClient(this.deployment.getGce().getAccount());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public void create() {
        Map<String, Object> context = new HashMap<>();
        context.put("deployment", this.deployment);

        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(this.deployment.getName() + ".gce" + ".yml"));
            writer.write(this.templates.template("gce.yml", context));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigFile config = new ConfigFile();
        config.setContent(this.templates.template("gce.yml", context));

        TargetConfiguration target = new TargetConfiguration();
        target.setConfig(config);

        com.google.api.services.deploymentmanager.model.Deployment d = new com.google.api.services.deploymentmanager.model.Deployment();
        d.setName(this.deployment.getName());
        d.setTarget(target);

        try {
            Operation o = this.client.getDeployment().deployments().insert(this.deployment.getGce().getProject(), d).execute();
            System.out.println(o.getHttpErrorMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    @Override
    public void destroy() {

    }

}
