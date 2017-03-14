package eu.mjelen.openshifter.provider.digitalocean;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("digitalocean")
public class DigitalOceanProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(Deployment deployment) {
        return null;
    }

}
