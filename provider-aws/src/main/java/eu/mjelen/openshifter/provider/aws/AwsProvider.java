package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider("aws")
public class AwsProvider implements ClusterProvider<Deployment> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Cluster analyze(Deployment deployment) {
        this.logger.debug("terraform plan");
        return null;
    }

}
