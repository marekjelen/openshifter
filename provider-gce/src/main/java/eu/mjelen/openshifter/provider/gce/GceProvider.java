package eu.mjelen.openshifter.provider.gce;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("gce")
public class GceProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(Deployment deployment) {
        return new GceCluster(deployment);
    }

}
