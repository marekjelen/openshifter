package eu.mjelen.openshifter.task.installer;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;

import javax.inject.Inject;

@Task(parent = InstallerTask.class)
@Target("master")
public class OcuTask {

    @Inject
    private Connection connection;

    @Inject
    private Deployment deployment;

    public void execute() {
        if(!this.connection.exec("[ -f oc ]").success()) {
            this.connection.exec("curl -L -o oc.tar.gz https://github.com/openshift/origin/releases/download/v1.4.0/openshift-origin-client-tools-v1.4.0-208f053-linux-64bit.tar.gz");
            this.connection.exec("tar xf oc.tar.gz");
            this.connection.exec("mv openshift-origin-client-tools-*/* .");
            this.connection.exec("rm -rf openshift-origin-client-tools-*");
            this.connection.exec("rm -f oc.tar.gz");
        }

        this.connection.upload("/etc/sysconfig/docker", getClass().getResourceAsStream("/ocu_docker.txt"));
        this.connection.exec("systemctl restart docker");

        this.connection.exec("./oc cluster down");

        String params = "";

        if(deployment.getType().equals("ocp")) {
            params += "--image=registry.access.redhat.com/openshift3/ose ";
        }

        params += "--version=" + deployment.getRelease() + " ";

        if(deployment.getDomain() != null) {
            String suffix = deployment.getName() + "." + deployment.getDomain();
            params += "--public-hostname=console." + suffix + " --routing-suffix=apps." + suffix + " ";
        }

        this.connection.exec("./oc cluster up " + params);

        System.exit(0);
    }

}
