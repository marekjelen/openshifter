package eu.mjelen.warden.ssh;

import eu.mjelen.warden.api.Connections;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;
import eu.mjelen.warden.api.server.Connection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SshConnections implements Connections {

    private final Cluster cluster;
    private final Map<String, Connection> connections = new HashMap<>();

    public SshConnections(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public List<Connection> getConnections(String labels) {
        List<? extends Instance> instances;
        if("all".equals(labels)) {
            instances = this.cluster.instances();
        } else {
            List<String> tags = Arrays.asList(labels.split(","));

            instances = this.cluster.instances().stream().filter(instance -> {
                return instance.getTags().containsAll(tags);
            }).collect(Collectors.toList());
        }

        return instances.stream().map(instance -> {
            String address = instance.getAddress();
            if(this.connections.containsKey(address)) {
                return this.connections.get(address);
            } else {
                SshConnection connection = new SshConnection();
                connection.connect(instance.getUsername(), address, instance.getPassword());
                this.connections.put(address, connection);
                return connection;
            }
        }).collect(Collectors.toList());
    }
}
