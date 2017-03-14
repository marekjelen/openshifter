package eu.mjelen.openshifter.provider.byo;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("byo")
public class ByoProvider implements ClusterProvider<Deployment> {

    @Override
    public Cluster analyze(Deployment descriptor) {
        return new ByoCluster(descriptor);
    }

}
