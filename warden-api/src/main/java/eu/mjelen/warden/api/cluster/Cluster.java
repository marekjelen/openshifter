package eu.mjelen.warden.api.cluster;

import java.util.List;

public interface Cluster {

    Instance instance(String label);

    List<? extends Instance> instances();
    List<? extends Instance> instances(String label);

    void validate();

    void create();

    void destroy();

}
