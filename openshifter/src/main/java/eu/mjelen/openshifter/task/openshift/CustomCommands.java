package eu.mjelen.openshifter.task.openshift;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;

import javax.inject.Inject;
import java.util.List;

@Task
@Dependency(CustomTemplates.class)
@Target("master")
public class CustomCommands {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    public void execute() {
        List<String> execute = this.deployment.getExecute();
        if(execute != null) {
            execute.forEach(cmd -> {
                this.remote.exec("oc " + cmd);
            });
        }
    }

}
