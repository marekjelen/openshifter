package eu.mjelen.openshifter.task.openshift;

import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.Map;

@Task
@Dependency(RobotUser.class)
@Target("master")
public class OpenShiftMaster {

    @Inject
    private Connection remote;

    @Inject
    private Cluster cluster;

    @Inject
    private Logger logger;

    @Inject
    private OpenShiftClient openshift;

    @SuppressWarnings("unchecked")
    public void execute() {
        Yaml yaml = new Yaml();

        String data = this.remote.download("/etc/origin/master/master-config.yaml");

        if(this.cluster.instances().size() == 1) {
            this.logger.info("Keep pods on infra as there are no nodes.");
            return;
        }

        Map<String, Object> config = (Map<String, Object>) yaml.load(data);

        ((Map<String, Object>) config.get("projectConfig")).put("defaultNodeSelector", "region=primary");

        this.remote.upload("/etc/origin/master/master-config.yaml", yaml.dump(config));

        this.logger.info("Restarting OpenShift master");

        this.remote.exec("systemctl restart origin-master");

        this.logger.info("OpenShift master has been reconfigured");

        while(true) {
            try {
                this.openshift.pods().list();
                break;
            }catch (KubernetesClientException e) {
                if(e.getCause().getClass() != ConnectException.class) {
                    break;
                }
                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

}
