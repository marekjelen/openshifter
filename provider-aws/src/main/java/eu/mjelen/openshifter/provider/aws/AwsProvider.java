package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;

@Provider("aws")
public class AwsProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(ClusterMap map, Deployment deployment) {
        return null;
    }

}
