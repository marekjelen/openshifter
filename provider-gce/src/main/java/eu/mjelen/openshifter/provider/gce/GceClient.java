package eu.mjelen.openshifter.provider.gce;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.deploymentmanager.DeploymentManager;
import com.google.api.services.dns.Dns;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class GceClient {

    private final NetHttpTransport transport;
    private final JacksonFactory json;
    private final Compute compute;
    private final Dns dns;
    private final DeploymentManager deployment;
    private GoogleCredential credential;

    public GceClient(String filename) throws GeneralSecurityException, IOException {
        this.transport = GoogleNetHttpTransport.newTrustedTransport();
        this.json = new JacksonFactory();
        this.credential = GoogleCredential.fromStream(new FileInputStream(filename));

        this.credential = this.credential.createScoped(Arrays.asList(
                "https://www.googleapis.com/auth/compute",
                "https://www.googleapis.com/auth/ndev.clouddns.readwrite"
        ));

        this.compute = new Compute.Builder(transport, json, credential).setApplicationName("openshifter").build();
        this.dns = new Dns.Builder(transport, json, credential).setApplicationName("openshifter").build();
        this.deployment = new DeploymentManager.Builder(transport, json, credential).setApplicationName("openshifter").build();
    }

    public Compute getCompute() {
        return compute;
    }

    public Dns getDns() {
        return dns;
    }

    public DeploymentManager getDeployment() {
        return deployment;
    }

    private Object authorize() {
        return null;
    }
}
