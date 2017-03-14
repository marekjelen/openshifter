package eu.mjelen.openshifter.task.component;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.task.openshift.OpenShiftMaster;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.util.Map;

@Task
@Dependency(OpenShiftMaster.class)
@Target("master")
public class NodePorts {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    @SuppressWarnings("unchecked")
    public void execute() {
        if(!this.deployment.getComponents().getOrDefault("nodePorts", false)) {
            this.logger.info("NodePorts are not required");
            return;
        }
        Yaml yaml = new Yaml();

        String data = this.remote.download("/etc/origin/master/master-config.yaml");

        if(data.contains("servicesNodePortRange: 30000-32767")) {
            this.logger.info("NodePorts are already enabled");
            return;
        }

        Map<String, Object> config = (Map<String, Object>) yaml.load(data);

        ((Map<String, Object>) config.get("kubernetesMasterConfig")).put("servicesNodePortRange", "30000-32767");

        this.remote.upload("/etc/origin/master/master-config.yaml", yaml.dump(config));

        this.logger.info("Restarting OpenShift master");

        this.remote.exec("systemctl restart origin-master");

        this.logger.info("OpenShift master has been reconfigured");
    }

}
