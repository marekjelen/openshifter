package eu.mjelen.openshifter.task.component;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.api.PVs;
import eu.mjelen.openshifter.task.openshift.OpenShiftMaster;
import eu.mjelen.warden.templates.Templates;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.server.Connection;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Task
@Dependency(OpenShiftMaster.class)
@Target("master")
public class PersistentVolumes {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    @Inject
    private Templates templates;

    @Inject
    private OpenShiftClient openshift;

    public void execute() {
        if(!this.deployment.getComponents().getOrDefault("pvs", false)) {
            this.logger.info("PVs are not enabled");
            return;
        }

        PVs pvs = this.deployment.getPvs();
        if(pvs == null) return;

        if(!"host".equals(pvs.getType())) {
            this.logger.info("ATM, only hostPath is available, specify: type: host");
            return;
        }

        if(this.deployment.getNodes().getCount() > 0 || this.deployment.getNodes().getInfra()) {
            this.logger.info("ATM, only hostPath is available, and for that All-In-One deployment is required");
            return;
        }

        if(!this.remote.exec("pvs").getStdout().contains("/dev/sdc")) {
            this.logger.info("Setting up PVs disk");
            this.remote.exec("pvcreate /dev/sdc");
            this.remote.exec("vgcreate PVS /dev/sdc");
            this.remote.exec("lvcreate -n PVS -l 100%FREE PVS");
            this.remote.exec("mkfs.xfs /dev/PVS/PVS");
            this.remote.exec("mkdir /pvs");
            this.remote.exec("mount /dev/PVS/PVS /pvs");
            this.remote.exec("chmod 777 /pvs");
            this.logger.info("PVs disk setup complete");
        }

        IntStream.rangeClosed(1, pvs.getCount()).forEach(i -> {
            String id = String.valueOf(i);
            if(id.length() == 1) id = "0" + id;
            String name = "host-pv-" + id;

            String dir = "/pvs/" + name;

            Map<String, String> context = new HashMap<>();
            context.put("name", name);
            context.put("dir", dir);
            context.put("size", pvs.getSize().toString());

            this.remote.exec("mkdir -p " + dir);
            this.remote.exec("chmod 777 " + dir);
            this.remote.exec("chcon -Rt svirt_sandbox_file_t " + dir);

            String content = templates.template("hostPath.ftl", context);
            this.remote.upload("host-pv.yml", content);
            this.remote.exec("/usr/local/bin/oc create -f host-pv.yml");
        });
    }

}
