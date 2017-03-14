package eu.mjelen.openshifter.provider.linode;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("linode")
public class LinodeProvider implements ClusterProvider<Deployment> {

    @Override
    public Cluster analyze(Deployment deployment) {
        return new LinodeCluster(deployment);
    }

}
