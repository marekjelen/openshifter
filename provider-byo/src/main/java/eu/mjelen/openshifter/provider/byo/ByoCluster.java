package eu.mjelen.openshifter.provider.byo;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ByoCluster implements Cluster {

    private final Deployment deployment;
    private final List<ByoInstance> list = new LinkedList<>();
    private final Map<String, ByoInstance> instances = new HashMap<>();
    private final Map<String, List<ByoInstance>> labels = new HashMap<>();

    public ByoCluster(Deployment deployment) {
        this.deployment = deployment;
        Map<String, List<String>> servers = this.deployment.getByo().getServers();
        servers.keySet().forEach(tag -> {
            this.labels.put(tag, new LinkedList<>());
            servers.get(tag).forEach(ip -> {
                if(!this.instances.containsKey(ip)) {
                    this.instances.put(ip, new ByoInstance(ip));
                    this.list.add(this.instances.get(ip));
                }
                this.instances.get(ip).addTag(tag);
                this.labels.get(tag).add(this.instances.get(ip));
            });
        });
    }

    @Override
    public Instance instance(String label) {
        return instances(label).get(0);
    }

    @Override
    public List<? extends Instance> instances() {
        return this.list;
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return this.labels.get(label);
    }

    @Override
    public void validate() {
        // not required
    }

    @Override
    public void create() {
        // no required
    }

    @Override
    public void destroy() {
        // no required
    }

    @Override
    public String toString() {
        return "ByoCluster{" +
                "deployment=" + deployment +
                ", list=" + list +
                ", instances=" + instances +
                ", labels=" + labels +
                '}';
    }
}
