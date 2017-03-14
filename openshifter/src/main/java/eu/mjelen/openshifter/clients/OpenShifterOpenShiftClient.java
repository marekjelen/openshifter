package eu.mjelen.openshifter.clients;

import eu.mjelen.warden.api.cluster.Cluster;
import io.fabric8.openshift.client.OpenShiftClient;

public class OpenShifterOpenShiftClient extends OpenShifterKubernetesClient {

    private OpenShiftClient client;

    public OpenShifterOpenShiftClient(Cluster cluster, String token) {
        super(cluster, token);
    }

    @Override
    public OpenShiftClient getClient() {
        if(this.client == null) {
            this.client = super.getClient().adapt(OpenShiftClient.class);
        }
        return this.client;
    }

}
