package eu.mjelen.openshifter.task.system;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.server.Connection;
import eu.mjelen.warden.api.server.ExecResult;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Dependency(SSH.class)
@Target("all")
public class Docker {

    @Inject
    private Connection remote;

    @Inject
    private Logger logger;

    @Inject
    private Deployment deployment;

    public void execute() {
        if(this.remote.exec("yum list installed | grep docker").success()) {
            this.logger.info("Docker is installed");
        } else {
            this.remote.exec("yum install -y docker");
        }

        ExecResult info = this.remote.exec("docker info");

        if(info.success() && info.getStdout().contains("Storage Driver: devicemapper")) {
            this.logger.info("Docker is configured");
        } else {
            this.remote.exec("echo DEVS=/dev/sdb >> /etc/sysconfig/docker-storage-setup");
            this.remote.exec("echo VG=DOCKER >> /etc/sysconfig/docker-storage-setup");
            this.remote.exec("systemctl stop docker");
            this.remote.exec("rm -rf /var/lib/docker");
            this.remote.exec("wipefs --all /dev/sdb");
            this.remote.exec("docker-storage-setup");
            this.remote.exec("systemctl start docker");
        }

        if(this.deployment.getDocker() != null) {
            eu.mjelen.openshifter.api.Docker docker = this.deployment.getDocker();
            if(docker.getPrime() != null) {
                docker.getPrime().forEach(image -> {
                    this.logger.info("Pulling image {}", image);
                    ExecResult res = this.remote.exec("docker pull " + image);
                    if(res.success()) {
                        this.logger.info("Successfully pulled image {}", image);
                    } else {
                        this.logger.info("Failed to pull image {}", image);
                        this.logger.info(res.getStdout());
                        this.logger.info(res.getStderr());
                    }
                });
            }
        }
    }

}
