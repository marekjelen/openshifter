package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;

@Provider("aws")
public class AwsProvider implements ClusterProvider<Deployment> {

    public Cluster analyze(Deployment deployment) {
        return null;
    }

}
