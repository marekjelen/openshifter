package eu.mjelen.openshifter.task.workshop;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.api.UsersRandom;
import eu.mjelen.openshifter.task.openshift.CustomCommands;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.server.Connection;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.stream.IntStream;

@Task
@Dependency(CustomCommands.class)
@Target("master")
public class Users {

    @Inject
    private Logger logger;

    @Inject
    private Connection remote;

    @Inject
    private Deployment deployment;

    public void execute() {
        String state = this.remote.download("users_setup");

        if("OK".equals(state)) {
            this.logger.info("OpenShift users are already setup");
            return;
        }

        if(this.deployment.getUsers() == null) return;

        if(this.deployment.getUsers().getAdmin() != null) {
            String username = this.deployment.getUsers().getAdmin().get("username");
            String password = this.deployment.getUsers().getAdmin().get("password");

            this.logger.info("Generating omnipotent user {} ", username);

            this.remote.exec("htpasswd -b /etc/origin/master/htpasswd " + username + " " + password);
            this.remote.exec("/usr/local/bin/oc adm policy add-cluster-role-to-user cluster-admin " + username);
        }

        if(this.deployment.getUsers().getRegular() != null) {
            this.deployment.getUsers().getRegular().forEach(user -> {
                String username = user.getUsername();
                String password = user.getPassword();

                this.logger.info("Generating user {} ", username);

                this.remote.exec("htpasswd -b /etc/origin/master/htpasswd " + username + " " + password);

                if (user.getSudoer()) {
                    this.logger.info("User {} is sudoer.", username);
                    this.remote.exec("/usr/local/bin/oc adm policy add-cluster-role-to-user sudoer " + username);
                }
            });
        }

        if(this.deployment.getUsers().getRandom() != null) {
            UsersRandom random = this.deployment.getUsers().getRandom();
            IntStream.rangeClosed(random.getMin(), random.getMax()).forEach(i -> {
                String id = String.valueOf(i);
                if(id.length() == 1) id = "0" + id;
                String username = random.getUsername() + id;
                String password = random.getPassword() + id;

                this.logger.info("Generating random user {} ", username);

                this.remote.exec("htpasswd -b /etc/origin/master/htpasswd " + username + " " + password);

                this.remote.exec("/usr/local/bin/oc new-project " + username);
                this.remote.exec("/usr/local/bin/oc adm policy add-role-to-user admin " + username + " -n " + username);

                random.getExecute().forEach(cmd -> {
                    this.logger.info("Executing '{}' for user {} ", cmd, username);
                    this.remote.exec("/usr/local/bin/oc " + cmd + " -n " + username);
                });
            });
        }

        this.remote.upload("users_setup", "OK");
    }

}
