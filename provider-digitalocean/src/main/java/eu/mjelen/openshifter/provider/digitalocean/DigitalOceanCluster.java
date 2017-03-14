package eu.mjelen.openshifter.provider.digitalocean;

import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;

import java.util.List;

public class DigitalOceanCluster implements Cluster {

    @Override
    public Instance instance(String label) {
        return null;
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return null;
    }

    @Override
    public List<? extends Instance> instances() {
        return null;
    }

    @Override
    public void validate() {

    }

    @Override
    public void create() {

    }

    @Override
    public void destroy() {

    }

}
