package eu.mjelen.openshifter.provider.linode;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;

@Provider("linode")
public class LinodeProvider implements ClusterProvider<Deployment> {

    @Override
    public Cluster analyze(ClusterMap map, Deployment deployment) {
        return new LinodeCluster(map, deployment);
    }

}
