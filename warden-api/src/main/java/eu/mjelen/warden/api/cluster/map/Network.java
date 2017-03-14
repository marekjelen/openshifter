package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.Set;

public class Network {

    private String name;

    private Set<SubNetwork> subNetworks = new HashSet<>();

    private Set<Firewall> firewalls = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addSubNetwork(SubNetwork subNetwork) {
        this.subNetworks.add(subNetwork);
    }

    public void addFirewall(Firewall firewall) {
        this.firewalls.add(firewall);
    }

    public String getName() {
        return name;
    }

    public Set<SubNetwork> getSubNetworks() {
        return subNetworks;
    }

    public Set<Firewall> getFirewalls() {
        return firewalls;
    }
}
