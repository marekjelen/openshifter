package eu.mjelen.openshifter.provider.digitalocean;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;

@Provider("digitalocean")
public class DigitalOceanProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(ClusterMap map, Deployment deployment) {
        return null;
    }

}
