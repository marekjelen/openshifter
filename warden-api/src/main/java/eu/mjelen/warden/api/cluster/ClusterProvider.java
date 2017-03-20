package eu.mjelen.warden.api.cluster;

import eu.mjelen.warden.api.cluster.map.ClusterMap;

public interface ClusterProvider<A> {

    Cluster analyze(A descriptor);

}
