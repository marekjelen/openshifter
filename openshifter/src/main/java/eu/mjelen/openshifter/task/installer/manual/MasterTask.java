package eu.mjelen.openshifter.task.installer.manual;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.task.installer.ManualTask;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;

import javax.inject.Inject;

@Task(parent = ManualTask.class)
@Target("master")
public class MasterTask {

    @Inject
    private Connection connection;

    @Inject
    private Deployment deployment;

    public void execute() {
        this.connection.exec("docker pull openshift/origin:" + this.deployment.getRelease());

        this.connection.exec("docker run -d --name origin" +
                " --privileged --pid=host --net=host" +
                " -v /:/rootfs:ro -v /var/run:/var/run:rw -v /sys:/sys -v /var/lib/docker:/var/lib/docker:rw" +
                " -v /var/lib/origin/openshift.local.config:/var/lib/origin/openshift.local.config:rslave" +
                " -v /var/lib/origin/openshift.local.etcd:/var/lib/origin/openshift.local.etcd:rslave" +
                " -v /var/lib/origin/openshift.local.volumes:/var/lib/origin/openshift.local.volumes:rslave" +
                " openshift/origin start");

        this.connection.upload("/usr/local/bin/oc", getClass().getResourceAsStream("/manual/oc.sh"));
        this.connection.exec("chmod +x /usr/local/bin/oc");
    }

}
