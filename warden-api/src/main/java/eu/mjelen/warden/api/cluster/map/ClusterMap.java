package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.Set;

public class ClusterMap {

    private String name;

    private Set<DnsZone> dnsZones = new HashSet<>();
    private Set<Network> networks = new HashSet<>();
    private Set<Machine> machines = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addDnsZone(DnsZone dnsZone) {
        this.dnsZones.add(dnsZone);
    }

    public void addNetwork(Network network) {
        this.networks.add(network);
    }

    public void addMachine(Machine machine) {
        this.machines.add(machine);
    }

    public String getName() {
        return name;
    }

    public Set<DnsZone> getDnsZones() {
        return dnsZones;
    }

    public Set<Network> getNetworks() {
        return networks;
    }

    public Set<Machine> getMachines() {
        return machines;
    }
}
