package eu.mjelen.warden.api.cluster;

import eu.mjelen.warden.api.cluster.map.ClusterMap;

public interface ClusterProvider<A> {

    Cluster analyze(ClusterMap map, A descriptor);

}
