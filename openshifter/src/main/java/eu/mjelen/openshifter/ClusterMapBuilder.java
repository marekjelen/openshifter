package eu.mjelen.openshifter;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.openshifter.api.NodesDisk;
import eu.mjelen.openshifter.api.NodesNode;
import eu.mjelen.warden.api.cluster.map.*;

import java.util.*;
import java.util.stream.LongStream;

public class ClusterMapBuilder {

    private final Deployment deployment;

    public ClusterMapBuilder(Deployment deployment) {
        this.deployment = deployment;
    }

    public ClusterMap build() {
        ClusterMap clusterMap = new ClusterMap();
        clusterMap.setName(this.deployment.getName());

        Network network = new Network();
        network.setName(this.deployment.getName());
        clusterMap.addNetwork(network);

        Firewall firewall = new Firewall();
        firewall.setName(this.deployment.getName());
        network.addFirewall(firewall);

        FirewallRule firewallRule;

        firewallRule = new FirewallRule();
        firewallRule.setName(this.deployment.getName() + "-all");
        firewall.addRule(firewallRule);
        firewallRule.addAllow("tcp/22");

        firewallRule = new FirewallRule();
        firewallRule.setName(this.deployment.getName() + "-internal");
        firewall.addRule(firewallRule);
        firewallRule.addSourceRange("10.128.0.0/9");
        firewallRule.addAllow("icmp");
        firewallRule.addAllow("tcp/0-65535");
        firewallRule.addAllow("udp/0-65535");

        firewallRule = new FirewallRule();
        firewallRule.setName(this.deployment.getName() + "-master");
        firewall.addRule(firewallRule);
        firewallRule.addTargetTag("master");
        firewallRule.addAllow("tcp/8443");

        firewallRule = new FirewallRule();
        firewallRule.setName(this.deployment.getName() + "-infra");
        firewall.addRule(firewallRule);
        firewallRule.addTargetTag("infra");
        firewallRule.addAllow("tcp/80,443" + (this.deployment.getComponents().getOrDefault("nodePorts", false) ? ",30000-32767" : ""));

        Region region = new Region(this.deployment.getNodes().getRegion());
        Zone zone = new Zone(this.deployment.getNodes().getZone(), region);

        Map<String, List<Machine>> machines = new HashMap<>();

        List<String> tags = new LinkedList<>();
        tags.addAll(Arrays.asList("master", "infra"));

        if(this.deployment.getNodes().getCount() != null && this.deployment.getNodes().getCount() > 0) {
            LongStream.rangeClosed(1, this.deployment.getNodes().getCount()).forEach(id -> {
                tags.add("node" + id);
            });
        } else {
            tags.add("node");
        }

        tags.forEach(tag -> {
            String id = "";

            if(tag.startsWith("node") && !tag.equals("node")) {
                id = tag.substring(4);
                tag = "node";
            }

            List<Machine> nodes = machines.computeIfAbsent(tag, key -> new LinkedList<>());

            if(tag.equals("infra") && !this.deployment.getNodes().getInfra()) {
                machines.get("master").forEach(machine -> { machine.addTag("infra"); });
                nodes.addAll(machines.get("master"));
                return;
            }

            if(tag.equals("node") && this.deployment.getNodes().getCount() == 0) {
                machines.get("master").forEach(machine -> { machine.addTag("node"); });
                nodes.addAll(machines.get("master"));
                return;
            }

            Machine machine = new Machine();
            machine.setName(this.deployment.getName() + "-" + tag + id);
            machine.setType(this.deployment.getNodes().getType());
            machine.setNetwork(network);
            machine.setZone(zone);
            machine.addTag(tag);

            clusterMap.addMachine(machine);
            nodes.add(machine);

            if(this.deployment.getNodes().getNodes().containsKey(tag)) {
                NodesNode node = this.deployment.getNodes().getNodes().get(tag);
                if(node.getType() != null) machine.setType(node.getType());
            }

            this.deployment.getSsh().getKeys().forEach(key -> {
                SshKey sshKey = new SshKey(key);
                sshKey.setUsername("openshift");
                machine.addKey(sshKey);
            });

            IpAddress address;
            address = new IpAddress();
            address.setName(this.deployment.getName() + "-" + tag + id);
            address.setEphemeral(false);
            address.setInternal(false);
            machine.addAddress(address);

            List<NodesDisk> disks = this.deployment.getNodes().getDisks();

            if(this.deployment.getNodes().getNodes().containsKey(tag)) {
                if(this.deployment.getNodes().getNodes().get(tag).getDisks() != null) {
                    disks.addAll(this.deployment.getNodes().getNodes().get(tag).getDisks());
                }
            }

            if(disks.size() == 0) {
                for(int x = 1; x <= 2; x++) {
                    Disk disk = new Disk();
                    disk.setName(this.deployment.getName() + "-" + tag + id + "-" + x);
                    disk.setType("ssd");
                    disk.setBoot(x == 1);
                    if(x == 1) {
                        disk.setSource("centos-7");
                        if (this.deployment.getType().equals("ocp")) {
                            disk.setName("rhel-7");
                        }
                    } else {
                        disk.setSize(100L);
                    }
                    machine.addDisk(disk);
                }
                if(tag.equals("infra") && this.deployment.getComponents().getOrDefault("pvs", false)) {
                    Disk disk = new Disk();
                    disk.setName(this.deployment.getName() + "-" + tag + id + "-" + 3);
                    disk.setType("ssd");
                    disk.setSize(100L);
                    disk.setBoot(false);
                    machine.addDisk(disk);
                }
            } else {
                for(int x = 0; x < disks.size(); x++) {
                    Disk disk = new Disk();
                    disk.setName(this.deployment.getName() + "-" + tag + id + "-" + x);
                    disk.setType(disks.get(x).getType());
                    disk.setSize(disks.get(x).getSize());
                    disk.setBoot(disks.get(x).getBoot());
                    if(disks.get(x).getBoot()) {
                        disk.setSource("centos-7");
                        if (this.deployment.getType().equals("ocp")) {
                            disk.setName("rhel-7");
                        }
                    }
                    machine.addDisk(disk);
                }
            }

        });


        if(this.deployment.getDns().getZone() != null) {
            DnsZone dnsZone = new DnsZone();
            dnsZone.setName(this.deployment.getDns().getZone());
            clusterMap.addDnsZone(dnsZone);

            String dnsSuffix = this.deployment.getName() + "." + this.deployment.getDns().getSuffix() + ".";

            DnsRecord dnsRecord;

            dnsRecord = new DnsRecord();
            dnsRecord.setName("console." + dnsSuffix);
            dnsRecord.setTtl(300);
            dnsRecord.setType("A");
            dnsRecord.setMachine("master");
            dnsZone.addRecord(dnsRecord);

            dnsRecord = new DnsRecord();
            dnsRecord.setName("*.apps." + dnsSuffix);
            dnsRecord.setTtl(300);
            dnsRecord.setType("A");
            dnsRecord.setMachine("infra");
            dnsZone.addRecord(dnsRecord);
        }

        return clusterMap;
    }
}
