package eu.mjelen.openshifter.task.installer;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.cli.Arguments;
import eu.mjelen.openshifter.task.system.SystemSetup;
import eu.mjelen.warden.api.Context;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Task;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Dependency(SystemSetup.class)
public class InstallerTask {

    @Inject
    private Deployment deployment;

    @Inject
    private Context context;

    @Inject
    private Arguments arguments;

    @Inject
    private Logger logger;

    public void execute() {
        this.context.skipChildTasks();

        if(this.arguments.hasFlag("skip_installer")) {
            this.logger.info("Skipping installer");
            return;
        }

        String installer = this.deployment.getInstaller();

        this.logger.info("Executing installer: {}", installer);

        if("ansible".equals(installer)) {
            this.context.execute(AnsibleTask.class);
        }
        if("ocu".equals(installer)) {
            this.context.execute(OcuTask.class);
        }
        if("manual".equals(installer)) {
            this.context.execute(ManualTask.class);
        }
        this.context.execute(VerifySshTask.class);
    }
}
