package eu.mjelen.openshifter.task.openshift;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;

import javax.inject.Inject;
import java.util.List;

@Task
@Dependency(BasicConfiguration.class)
@Target("master")
public class CustomTemplates {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    public void execute() {
        List<String> templates = this.deployment.getTemplates();
        if(templates != null) {
            templates.forEach(template -> {
                this.remote.exec("curl -o template.tmp " + template);
                this.remote.exec("oc create -f template.tmp -n openshift");
            });
        }
    }

}
