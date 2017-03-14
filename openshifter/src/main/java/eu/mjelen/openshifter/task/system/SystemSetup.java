package eu.mjelen.openshifter.task.system;

import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;

@Task
@Dependency(Docker.class)
@Target("all")
public class SystemSetup {

    @Inject
    private Connection remote;

    @Inject
    private Logger logger;

    private final static String SOURCE = "http://cdn.azul.com/zulu/bin/zulu8.19.0.1-jdk8.0.112-linux_x64.tar.gz";

    public void execute() {

        if(this.remote.exec("yum list installed | grep python-six").success()) {
            this.logger.info("python-six is installed");
        } else {
            this.remote.exec("yum install -y python-six");
        }

        if(this.remote.exec("[ -f /opt/java/bin/java ]").success()) {
            return;
        }
        this.remote.exec("curl -o /opt/java.tar.gz " + SOURCE);
        this.remote.exec("cd /opt; tar -xf java.tar.gz; mv zulu8* java; rm java.tar.gz");
        this.remote.exec("/opt/java/bin/java -version");
    }
}
