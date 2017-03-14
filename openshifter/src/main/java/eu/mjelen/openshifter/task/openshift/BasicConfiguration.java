package eu.mjelen.openshifter.task.openshift;

import eu.mjelen.openshifter.task.component.PersistentVolumes;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Dependency(PersistentVolumes.class)
@Target("all")
public class BasicConfiguration {

    @Inject
    private Connection remote;

    @Inject
    private Logger logger;

    public void execute() {
        this.remote.connect();
        this.logger.info("Basic configuration of OpenShift has been completed");
    }

}
