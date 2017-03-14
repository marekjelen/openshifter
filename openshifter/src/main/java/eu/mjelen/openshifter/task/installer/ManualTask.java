package eu.mjelen.openshifter.task.installer;

import eu.mjelen.openshifter.task.installer.manual.MasterTask;
import eu.mjelen.warden.api.Context;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;

import javax.inject.Inject;

@Task(parent = InstallerTask.class)
@Target("master")
public class ManualTask {

    @Inject
    private Context context;

    public void execute() {
        this.context.execute(MasterTask.class);
    }

}
