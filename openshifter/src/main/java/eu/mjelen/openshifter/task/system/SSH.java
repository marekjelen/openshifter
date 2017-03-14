package eu.mjelen.openshifter.task.system;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Target("all")
public class SSH {

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    @Inject
    private Logger logger;

    public void execute() {
        if(this.remote.sudo("[ -f /root/.ssh/authorized_keys ]").success()) {
            this.logger.info("Root already has authorized keys");
        } else {
            this.remote.sudo("mkdir -p /root/.ssh");
            String keys = deployment.getSsh().getKeys().stream().reduce("", (ks, key) -> {
                return ks + key + " uploaded\n";
            });
            this.remote.upload("authorized_keys", keys);
            this.remote.sudo("cp authorized_keys /root/.ssh");
        }

        if(this.remote.sudo("cat /etc/ssh/sshd_config").getStdout().contains("PermitRootLogin no")) {
            if(this.remote.upload("sshd_config", getClass().getResourceAsStream("/sshd_config"))) {
                this.remote.sudo("mv sshd_config /etc/ssh/sshd_config");
                this.remote.sudo("systemctl restart sshd");
            }
        } else {
            this.logger.info("Root is allowed to login");
        }

        this.remote.connect("root");
    }

}
