package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Machine {

    private String name;

    private Zone zone;

    private String type;

    private List<Disk> disks = new LinkedList<>();

    private Network network;
    private Set<IpAddress> addresses = new HashSet<>();
    private Set<String> tags = new HashSet<>();

    private Set<SshKey> sshKeys = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addDisk(Disk disk) {
        this.disks.add(disk);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void addAddress(IpAddress address) {
        this.addresses.add(address);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void addKey(SshKey key) {
        this.sshKeys.add(key);
    }

    public String getName() {
        return name;
    }

    public Zone getZone() {
        return zone;
    }

    public String getType() {
        return type;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public Set<IpAddress> getAddresses() {
        return addresses;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<SshKey> getSshKeys() {
        return sshKeys;
    }
}
