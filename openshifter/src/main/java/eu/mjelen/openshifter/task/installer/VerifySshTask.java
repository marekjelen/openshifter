package eu.mjelen.openshifter.task.installer;

import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;

import javax.inject.Inject;

@Task(parent = InstallerTask.class)
@Target("all")
public class VerifySshTask {

    @Inject
    private Connection connection;

    public void execute() {
        this.connection.connect();
    }

}
