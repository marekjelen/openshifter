package eu.mjelen.openshifter.provider.gce;

import com.google.common.collect.Lists;
import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;
import eu.mjelen.openshifter.provider.gce.components.*;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import eu.mjelen.warden.api.cluster.map.IpAddress;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class GceCluster implements Cluster {

    private final Deployment deployment;
    private final Project project;

    private final List<GceComponent> components = new LinkedList<>();
    private PrivateNetwork network;
    private final List<Machine> nodes = new LinkedList<>();

    private GceClient client;

    private Map<String, String> sourceImages = new HashMap<>();

    {
        sourceImages.put("centos-7", "projects/centos-cloud/global/images/family/centos-7");
        sourceImages.put("rhel-7", "projects/rhel-cloud/global/images/family/rhel-7");
    }

    public GceCluster(ClusterMap map, Deployment deployment) {
        this.deployment = deployment;

        try {
            this.client = new GceClient(this.deployment.getGce().getAccount());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        this.project = new Project(this.deployment.getGce().getProject());

        map.getNetworks().forEach(ntwrk -> {
            this.network = new PrivateNetwork(ntwrk.getName());
            this.network.setProject(this.project);
            this.components.add(network);

            ntwrk.getFirewalls().forEach(frwl -> {
                frwl.getFirewallRules().forEach(frwlr -> {
                    FirewallRule rule = new FirewallRule(frwlr.getName());
                    this.network.addFirewallRule(rule);
                    this.components.add(rule);
                    rule.setProject(getProject());
                    frwlr.getSourceRanges().forEach(rule::addSourceRange);
                    frwlr.getTargetTags().forEach(rule::addTargetTag);
                    frwlr.getAllow().forEach(allow -> {
                        if(allow.contains("/")) {
                            String[] segments = allow.split("/");
                            rule.allow(segments[0], segments[1].split(","));
                        } else {
                            rule.allow(allow);
                        }

                    });
                });
            });
        });

        map.getMachines().forEach(mchn -> {
            Region region;

            if(mchn.getZone().getRegion() == null || mchn.getZone().getRegion().getName() == null) {
                String[] segment = mchn.getZone().getName().split("-");
                region = new Region(segment[0] + "-" + segment[1]);
            } else {
                region = new Region(mchn.getZone().getRegion().getName());
            }

            Zone zone = new Zone(region, mchn.getZone().getName());

            IPAddress address = null;

            for(IpAddress addr : mchn.getAddresses()) {
                address = new IPAddress(addr.getName(), zone);
                address.setProject(this.project);
                this.components.add(address);
            }

            Machine machine = new Machine(mchn.getName(), mchn.getType(), zone, this.network);
            machine.setProject(this.project);
            mchn.getTags().forEach(machine::addTag);
            if(address != null) {
                machine.setIPAddress(address);
            }

            mchn.getDisks().forEach(dsk -> {
                PersistentDisk disk = new PersistentDisk(dsk.getName(), zone);
                this.components.add(disk);
                disk.setProject(this.project);
                if(dsk.getSource() != null) {
                    disk.setSource(this.sourceImages.get(dsk.getSource()));
                }
                disk.setBoot(dsk.isBoot());
                if(dsk.getSize() != null) {
                    disk.setSize(dsk.getSize());
                }
                machine.addDisk(disk);
            });

            mchn.getSshKeys().forEach(key -> {
                machine.addSshKey(new SshKey(key.getUsername(), key.getType() + " " + key.getKey()));
            });

            this.components.add(machine);
            this.nodes.add(machine);
        });

        map.getDnsZones().forEach(zone -> {
            zone.getRecords().forEach(record -> {
                DomainName domainName = new DomainName();
                this.components.add(domainName);
                domainName.setProject(this.project);
                domainName.setZone(zone.getName());
                domainName.setTtl(record.getTtl());
                domainName.setType(record.getType());
                domainName.setMachine((Machine) instance(record.getMachine()));
                domainName.setName(record.getName());

            });
        });
    }

    public List<Machine> instances() {
        return nodes;
    }

    @Override
    public Instance instance(String label) {
        Optional<Machine> item = this.nodes.stream().filter(node -> {
            return node.getTags().contains(label);
        }).findFirst();

        return item.orElse(null);
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return this.nodes.stream().filter(node -> {
            return node.getTags().contains(label);
        }).collect(Collectors.toList());
    }

    @Override
    public void validate() {
        this.components.forEach(component -> {
            component.setClient(this.client);
            component.load();
        });
    }

    @Override
    public void create() {
        this.components.forEach(component -> {
            component.setClient(this.client);
            component.create();
        });
    }

    @Override
    public void destroy() {
        Lists.reverse(this.getComponents()).forEach(component -> {
            component.setClient(this.client);
            component.destroy();
        });
    }

    public Project getProject() {
        return project;
    }

    public List<GceComponent> getComponents() {
        return components;
    }

}
