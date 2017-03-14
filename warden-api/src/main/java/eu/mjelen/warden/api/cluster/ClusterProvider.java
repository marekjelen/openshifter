package eu.mjelen.warden.api.cluster;

public interface ClusterProvider<A> {

    Cluster analyze(A descriptor);

}
