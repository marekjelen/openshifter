package eu.mjelen.openshifter.task.component;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.task.openshift.BasicConfiguration;
import eu.mjelen.warden.templates.Templates;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Task
@Dependency(BasicConfiguration.class)
@Target("master")
public class Cockpit {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    @Inject
    private Templates templates;

    public void execute() {
        if(!this.deployment.getComponents().getOrDefault("cockpit", false)) {
            this.logger.info("Cockpit is not enabled");
            return;
        }

        if(this.remote.exec("/usr/local/bin/oc get dc openshift-cockpit").success()) {
            this.logger.info("Cockpit is already setup");
            return;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("deployment", this.deployment);

        this.remote.upload("cockpit.json", templates.template("cockpit.ftl", context));
        this.remote.exec("/usr/local/bin/oc create -f cockpit.json -n openshift");
        this.remote.exec("/usr/local/bin/oc new-app openshift-cockpit -n default");

        this.logger.info("Cockpit deployed");
    }

}
