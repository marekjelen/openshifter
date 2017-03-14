package eu.mjelen.openshifter.task.installer;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.templates.Templates;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Task(parent = InstallerTask.class)
@Target("master")
public class AnsibleTask {

    @Inject
    private Connection remote;

    @Inject
    private Templates templates;

    @Inject
    private Deployment deployment;

    @Inject
    private Cluster cluster;

    @Inject
    private Logger logger;

    public void install() {
        this.logger.info("Execution Ansible installer");

        this.logger.info("Cluster: {}", cluster);

        try {
            Map<String, Object> context = new HashMap<>();
            context.put("cluster", this.cluster);
            context.put("deployment", this.deployment);

            FileWriter writer = new FileWriter(new File(this.deployment.getName() + ".ini"));
            writer.write(this.templates.template("inventory.ftl", context));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder builder = new ProcessBuilder("ansible-playbook",  "-v", "-i", this.deployment.getName() + ".ini",
                System.getenv().getOrDefault("OPENSHIFT_ANSIBLE", "./openshift-ansible") + "/playbooks/byo/config.yml");

        Path root = Paths.get("").toAbsolutePath();
        builder.directory(root.toFile());

        builder.environment().put("ANSIBLE_HOST_KEY_CHECKING", "False");
        builder.environment().put("ANSIBLE_PRIVATE_KEY_FILE", root.resolve("private.key").toAbsolutePath().toString());

        this.logger.info("Working dir: {}", builder.directory().getAbsolutePath());
        this.logger.info("Running: {}", builder.command());

        try {
            Process process = builder.start();
            InputStream out = process.getInputStream();
            InputStream err = process.getErrorStream();

            FileWriter outFile = new FileWriter(new File(this.deployment.getName() + ".ansible.out.log"));
            FileWriter errFile = new FileWriter(new File(this.deployment.getName() + ".ansible.err.log"));

            read(out, (line) -> {
                try {
                    outFile.write(line + "\n");
                    outFile.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.logger.info("stdout: {}", line);
            });

            read(err, (line) -> {
                try {
                    errFile.write(line + "\n");
                    errFile.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.logger.info("stderr: {}", line);
            });

            while(process.isAlive()) {
                synchronized (this) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(process.exitValue() == 0) {
                //Todo: mark that installer finished successfully
            } else {
                this.logger.info("Execution failed, exit value: {}", process.exitValue());
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(InputStream stream, Consumer<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        callback.accept(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
