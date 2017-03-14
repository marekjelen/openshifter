package eu.mjelen.openshifter.provider.linode;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import synapticloop.linode.LinodeApi;

import java.util.*;
import java.util.stream.LongStream;

public class LinodeCluster implements Cluster {

    private final Deployment deployment;
    private LinodeApi client;

    private Map<String, List<LinodeInstance>> instances = new HashMap<>();
    private List<LinodeInstance> allInstances = new LinkedList<>();

    public LinodeCluster(ClusterMap map, Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public Instance instance(String label) {
        if(this.instances.get(label) == null || this.instances.get(label).size() == 0) {
            return null;
        }
        return this.instances.get(label).get(0);
    }

    @Override
    public List<? extends Instance> instances() {
        return this.allInstances;
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return this.instances.get(label);
    }

    @Override
    public void validate() {
        this.client = new LinodeApi(this.deployment.getLinode().getKey());

        String dc = this.deployment.getLinode().getDatacenter();
        Long plan = this.deployment.getLinode().getPlan();
        LinodeInstance master = addInstance(dc, plan, "master", "master");

        LinodeInstance infra;
        if(this.deployment.getNodes().getInfra()) {
            infra = addInstance(dc, plan, "infra", "infra");
        } else {
            infra = addToLabel(master, "infra");
        }

        if(this.deployment.getNodes().getCount() > 0) {
            LongStream.range(0, this.deployment.getNodes().getCount()).forEach(id -> {
                addInstance(dc, plan, "node-" + id, "node");
            });
        } else {
            addToLabel(master, "node");
        }

        this.allInstances.forEach(instance -> {
            instance.load();
        });
    }

    private LinodeInstance addInstance(String dc, Long plan, String name, String label) {
        name = this.deployment.getName() + "-" + name;
        LinodeInstance instance = new LinodeInstance(this.client, name, dc, plan);
        this.allInstances.add(instance);
        addToLabel(instance, label);
        return instance;
    }

    private LinodeInstance addToLabel(LinodeInstance instance, String label) {
        instance.addTag(label);
        this.instances.computeIfAbsent(label, k -> new LinkedList<>());
        this.instances.get(label).add(instance);
        return instance;
    }

    @Override
    public void create() {
        this.allInstances.forEach(instance -> {
            instance.create();
        });
    }

    @Override
    public void destroy() {
        this.allInstances.forEach(instance -> {
            instance.destroy();
        });
    }

}
