package eu.mjelen.openshifter.provider.gce.components;

import com.google.api.services.compute.model.*;
import com.google.api.services.compute.model.Instance;
import eu.mjelen.openshifter.provider.gce.GceComponent;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Machine extends GceComponent implements eu.mjelen.warden.api.cluster.Instance {

    private String name;

    private Zone zone;
    private String type;

    private PrivateNetwork network;
    private IPAddress address;
    private String internalAddress;

    private List<PersistentDisk> disks = new LinkedList<>();

    private List<SshKey> sshKeys = new LinkedList<>();
    private List<String> tags = new LinkedList<>();

    public Machine(String name, String type, Zone zone, PrivateNetwork network) {
        this.name = name;
        this.type = type;
        this.zone = zone;
        this.network = network;
    }

    public void addDisk(PersistentDisk disk) {
        this.disks.add(disk);
    }

    public void addSshKey(SshKey key) {
        this.sshKeys.add(key);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PrivateNetwork getNetwork() {
        return network;
    }

    public void setNetwork(PrivateNetwork network) {
        this.network = network;
    }

    public IPAddress getIPAddress() {
        return address;
    }

    public void setIPAddress(IPAddress address) {
        this.address = address;
    }

    public List<PersistentDisk> getDisks() {
        return disks;
    }

    public void setDisks(List<PersistentDisk> disks) {
        this.disks = disks;
    }

    public List<SshKey> getSshKeys() {
        return sshKeys;
    }

    public void setSshKeys(List<SshKey> sshKeys) {
        this.sshKeys = sshKeys;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public Operation doLoad() throws IOException {
//        this.address = new IPAddress(getName(), getZone());
//        this.address.setClient(getClient());
//        this.address.setProject(getProject());
//        this.address.load();

        Instance instance = compute().instances().get(getProject().getName(), getZone().getZone(), getName()).execute();
        setUrl(instance.getSelfLink());
        setTags(instance.getTags().getItems());
        this.internalAddress = instance.getNetworkInterfaces().get(0).getNetworkIP();
        return null;
    }

    @Override
    public Operation doCreate() throws Exception {
        Instance instance = new Instance();
        instance.setName(getName());
        instance.setMachineType("zones/" + getZone().getZone() + "/machineTypes/" + getType());

        Tags tags = new Tags();
        tags.setItems(getTags());
        instance.setTags(tags);

        List<AttachedDisk> ads = getDisks().stream().map(disk -> {
            AttachedDisk ad = new AttachedDisk();
            ad.setBoot(disk.getBoot());
            ad.setSource(disk.getUrl());
            return ad;
        }).collect(Collectors.toList());

        instance.setDisks(ads);

        NetworkInterface inet = new NetworkInterface();
        inet.setNetwork(this.network.getUrl());
        instance.setNetworkInterfaces(Collections.singletonList(inet));

        Metadata metadata = new Metadata();
        instance.setMetadata(metadata);

        List<Metadata.Items> items = getSshKeys().stream().map(sshKey -> {
            Metadata.Items item = new Metadata.Items();
            item.setKey("ssh-keys");
            item.setValue(sshKey.getUsername() + ":" + sshKey.getKey());
            return item;
        }).collect(Collectors.toList());

        metadata.setItems(items);

        if(getIPAddress() != null) {
            AccessConfig acs = new AccessConfig();
            acs.setName(getName());
            acs.setNatIP(getIPAddress().getAddress());
            inet.setAccessConfigs(Collections.singletonList(acs));
        }

        return compute().instances().insert(getProject().getName(), getZone().getZone(), instance).execute();
    }

    @Override
    public Operation doDestroy() throws Exception {
        return compute().instances().delete(getProject().getName(), getZone().getZone(), getName()).execute();
    }

    @Override
    public String getAddress() {
        return getIPAddress() == null ? null : getIPAddress().getAddress();
    }

    @Override
    public String getInternalAddress() {
        return this.internalAddress;
    }

    @Override
    public String getUsername() {
        return "openshift";
    }
}
