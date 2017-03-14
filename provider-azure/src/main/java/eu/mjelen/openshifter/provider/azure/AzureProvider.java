package eu.mjelen.openshifter.provider.azure;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("azure")
public class AzureProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(Deployment deployment) {
        return null;
    }

}
