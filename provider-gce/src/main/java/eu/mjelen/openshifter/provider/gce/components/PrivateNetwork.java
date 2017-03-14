package eu.mjelen.openshifter.provider.gce.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.Operation;
import eu.mjelen.openshifter.provider.gce.GceComponent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PrivateNetwork extends GceComponent {

    private String name;
    private Boolean autoCreateSubnetworks = true;

    @JsonIgnore
    private List<FirewallRule> firewallRules = new LinkedList<>();

    public PrivateNetwork(String name) {
        this.name = name;
    }

    public void addFirewallRule(FirewallRule rule) {
        this.firewallRules.add(rule);
        rule.setNetwork(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAutoCreateSubnetworks() {
        return autoCreateSubnetworks;
    }

    public void setAutoCreateSubnetworks(Boolean autoCreateSubnetworks) {
        this.autoCreateSubnetworks = autoCreateSubnetworks;
    }

    @Override
    public Operation doLoad() throws IOException {
        Network network = compute().networks().get(this.getProject().getName(), this.getName()).execute();
        setUrl(network.getSelfLink());
        return null;
    }

    @Override
    public Operation doCreate() throws IOException {
        Network network = new com.google.api.services.compute.model.Network();
        network.setName(this.getName());
        network.setAutoCreateSubnetworks(this.getAutoCreateSubnetworks());

        return compute().networks().insert(this.getProject().getName(), network).execute();
    }

    @Override
    public Operation doDestroy() throws Exception {
        return compute().networks().delete(this.getProject().getName(), this.getName()).execute();
    }
}
