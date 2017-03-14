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
public class RunAsRoot {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    public void execute() {
        if(!this.deployment.getComponents().getOrDefault("runAsRoot", false)) {
            this.logger.info("runAsRoot is not enabled");
            return;
        }

        this.logger.info("Enabling runAsRoot");

        this.remote.exec("/usr/local/bin/oc adm policy analyze-scc-to-group anyuid system:authenticated");

        this.logger.info("runAsRoot is enabled");
    }

}
