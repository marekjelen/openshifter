package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.Set;

public class Firewall {

    private String name;

    private Set<FirewallRule> firewallRules = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addRule(FirewallRule rule) {
        this.firewallRules.add(rule);
    }

    public String getName() {
        return name;
    }

    public Set<FirewallRule> getFirewallRules() {
        return firewallRules;
    }
}
