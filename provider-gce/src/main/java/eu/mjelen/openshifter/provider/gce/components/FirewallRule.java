package eu.mjelen.openshifter.provider.gce.components;

import com.google.api.services.compute.model.*;
import eu.mjelen.openshifter.provider.gce.GceComponent;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FirewallRule extends GceComponent {

    private String name;
    private PrivateNetwork network;

    private List<String> targetTags = new LinkedList<>();
    private List<String> sourceRanges = new LinkedList<>();

    private List<FirewallAllow> allows = new LinkedList<>();

    public FirewallRule(String name) {
        this.name = name;
    }

    public void addTargetTag(String tag) {
        this.targetTags.add(tag);
    }

    public void addSourceRange(String range) {
        this.sourceRanges.add(range);
    }

    public void allow(String protocol, String[] ports) {
        this.allows.add(new FirewallAllow(protocol, ports));
    }

    public void allow(String protocol) {
        allow(protocol, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrivateNetwork getNetwork() {
        return network;
    }

    public void setNetwork(PrivateNetwork network) {
        this.network = network;
    }

    public List<String> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<String> targetTags) {
        this.targetTags = targetTags;
    }

    public List<String> getSourceRanges() {
        return sourceRanges;
    }

    public void setSourceRanges(List<String> sourceRanges) {
        this.sourceRanges = sourceRanges;
    }

    public List<FirewallAllow> getAllows() {
        return allows;
    }

    public void setAllows(List<FirewallAllow> allows) {
        this.allows = allows;
    }

    @Override
    public Operation doLoad() throws IOException {
        Firewall rule = compute().firewalls().get(getProject().getName(), getName()).execute();
        setUrl(rule.getSelfLink());
        return null;
    }

    @Override
    public Operation doCreate() throws Exception {
        Firewall firewall = new Firewall();
        firewall.setName(getName());
        firewall.setNetwork(getNetwork().getUrl());

        if(getSourceRanges() != null && getSourceRanges().size() > 0) {
            firewall.setSourceRanges(getSourceRanges());
        }

        if(getTargetTags() != null && getTargetTags().size() > 0) {
            firewall.setTargetTags(getTargetTags());
        }

        if(getAllows() != null && getAllows().size() > 0) {
            List<Firewall.Allowed> alloweds = getAllows().stream().map(allow -> {
                Firewall.Allowed allowed = new Firewall.Allowed();
                allowed.setIPProtocol(allow.getProtocol());
                if(allow.getPorts() != null && allow.getPorts().length > 0) {
                    allowed.setPorts(Arrays.asList(allow.getPorts()));
                }
                return allowed;
            }).collect(Collectors.toList());

            firewall.setAllowed(alloweds);
        }

        return compute().firewalls().insert(getProject().getName(), firewall).execute();
    }

    @Override
    public Operation doDestroy() throws Exception {
        return compute().firewalls().delete(getProject().getName(), getName()).execute();
    }
}
