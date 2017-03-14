package eu.mjelen.openshifter.provider.gce;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;

@Provider("gce")
public class GceProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(ClusterMap map, Deployment deployment) {
        return new GceCluster(map, deployment);
    }

}
