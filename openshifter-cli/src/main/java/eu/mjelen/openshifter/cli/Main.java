package eu.mjelen.openshifter.cli;

import eu.mjelen.openshifter.ClusterMapBuilder;
import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.Warden;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import eu.mjelen.warden.templates.Templates;
import eu.mjelen.warden.api.server.Connection;
import eu.mjelen.warden.api.server.ExecResult;
import eu.mjelen.warden.ssh.SshConnections;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;

import java.io.File;
import java.util.List;

public class Main {

    private final Warden<Deployment> warden;
    private final Arguments arguments;

    public Main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("openshifter");

        parser.addArgument("action").type(String.class);
        parser.addArgument("name").type(String.class);
        parser.addArgument("--verbose").type(Boolean.class).action(net.sourceforge.argparse4j.impl.Arguments.storeConst()).setConst(false);
        parser.addArgument("--skip-installer").type(Boolean.class).action(net.sourceforge.argparse4j.impl.Arguments.storeConst()).setConst(true);
        parser.addArgument("--skip-infrastructure").type(Boolean.class).action(net.sourceforge.argparse4j.impl.Arguments.storeConst()).setConst(true);

        this.arguments = new Arguments(parser.parseArgs(args).getAttrs());

        this.warden = new Warden<>(Deployment.class);

        File file = new File(this.arguments.getName() + ".yml");

        if(file.exists()) {
            this.warden.loadFromFile(file.getAbsolutePath());
        } else {
            this.warden.loadFromFile(new File(this.arguments.getName() + ".json").getAbsolutePath());
        }

        if(this.warden.getDescriptor().getName() == null) {
            this.warden.getDescriptor().setName(this.arguments.getName());
        }

        // ClusterMap clusterMap = new ClusterMapBuilder(this.warden.getDescriptor()).build();

        this.warden.validateCluster();

        // if("create".equals(this.arguments.getAction())) {
        //     if(!this.arguments.hasFlag("skip_infrastructure")) {
        //         this.warden.buildCluster();
        //     }
        //
        //     this.warden.setContextBuilder(context -> {
        //         context.setInjection(Arguments.class, this.arguments);
        //         context.setInjection(Templates.class, new Templates() {
        //         });
        //     });
        //
        //     this.warden.execute();
        // }
        //
        // if("destroy".equals(this.arguments.getAction())) {
        //     if(!this.arguments.hasFlag("skip_infrastructure")) {
        //         this.warden.destroyCluster();
        //     }
        // }
        //
        // if("cleanup".equals(this.arguments.getAction())) {
        //     List<Connection> connections = new SshConnections(this.warden.getCluster()).getConnections("all");
        //
        //     connections.forEach(connection -> {
        //         connection.connect("root");
        //     });
        //
        //     connections.forEach(connection -> {
        //         ExecResult result;
        //         result = connection.exec("docker rm $(docker ps -q -f status=exited)");
        //         result = connection.exec("docker rmi $(docker images -q -f 'dangling=true')");
        //     });
        // }

        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        for(String arg : args) {
            if("--verbose".equals(arg)) {
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
            }
        }
        System.setProperty("java.util.logging.config.file", "logging.properties");
        new Main(args);
    }

}
