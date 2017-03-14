package eu.mjelen.openshifter.clients;

import eu.mjelen.warden.api.cluster.Cluster;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class OpenShifterKubernetesClient {

    private final Cluster cluster;
    private final String token;
    private KubernetesClient client;

    public OpenShifterKubernetesClient(Cluster cluster, String token) {
        this.cluster = cluster;
        this.token = token;
        reconnect();
    }

    private void reconnect() {
        ConfigBuilder config = new ConfigBuilder();
        config = config.withTrustCerts(true);

        config = config.withMasterUrl("https://" + this.cluster.instance("master").getAddress() + ":8443");
        config = config.withOauthToken(this.token);

        this.client = new DefaultKubernetesClient(config.build());
    }

    public KubernetesClient getClient() {
        return client;
    }

}
