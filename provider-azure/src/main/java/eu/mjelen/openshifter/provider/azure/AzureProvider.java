package eu.mjelen.openshifter.provider.azure;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;

@Provider("azure")
public class AzureProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(ClusterMap map, Deployment deployment) {
        return null;
    }

}
