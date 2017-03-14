package eu.mjelen.openshifter.task.component;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.task.openshift.BasicConfiguration;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Dependency(BasicConfiguration.class)
@Target("master")
public class HostPath {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    public void execute() {
        if(!this.deployment.getComponents().getOrDefault("hostPath", false)) {
            this.logger.info("hostPath is not enabled");
            return;
        }

        this.logger.info("Enabling hostPath");

        this.remote.exec("/usr/local/bin/oc adm policy analyze-scc-to-group hostaccess system:authenticated");

        this.logger.info("hostPath is enabled");
    }

}
