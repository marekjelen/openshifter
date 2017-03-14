package eu.mjelen.openshifter.provider.gce;

import com.google.common.collect.Lists;
import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;
import eu.mjelen.openshifter.provider.gce.components.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class GceCluster implements Cluster {

    private final Deployment deployment;
    private final Project project;
    private final String name;
    private final Region region;
    private final Zone zone;

    private final List<GceComponent> components = new LinkedList<>();
    private final PrivateNetwork network;
    private final List<PersistentDisk> disks = new LinkedList<>();
    private final List<Machine> nodes = new LinkedList<>();
    private final Machine master;
    private Machine infra;

    private GceClient client;

    private String sourceImage;

    public GceCluster(Deployment deployment) {
        this.deployment = deployment;

        try {
            this.client = new GceClient(this.deployment.getGce().getAccount());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        this.name = this.deployment.getName();
        this.region = new Region(this.deployment.getGce().getRegion());
        this.zone = new Zone(this.region, this.deployment.getGce().getZone());

        this.project = new Project(this.deployment.getGce().getProject());

        this.network = new PrivateNetwork(this.name);
        this.network.setProject(this.project);

        this.components.add(network);

        FirewallRule rule;

        rule = new FirewallRule(this.name +  "-all");
        this.network.addFirewallRule(rule);
        this.components.add(rule);
        rule.setProject(getProject());
        rule.allow("tcp", new String[] { "22"});

        rule = new FirewallRule(this.name + "-internal");
        this.network.addFirewallRule(rule);
        this.components.add(rule);
        rule.setProject(getProject());
        rule.addSourceRange("10.128.0.0/9");
        rule.allow("icmp");
        rule.allow("tcp", new String[] { "0-65535" });
        rule.allow("udp", new String[] { "0-65535" });

        rule = new FirewallRule(this.name + "-master");
        this.network.addFirewallRule(rule);
        this.components.add(rule);
        rule.setProject(getProject());
        rule.addTargetTag("master");
        rule.allow("tcp", new String[] { "8443" });

        rule = new FirewallRule(this.name + "-infra");
        this.network.addFirewallRule(rule);
        this.components.add(rule);
        rule.setProject(getProject());
        rule.addTargetTag("infra");
        if(this.deployment.getComponents().getOrDefault("nodePorts", false)) {
            rule.allow("tcp", new String[]{"80", "443", "30000-32767"});
        } else {
            rule.allow("tcp", new String[]{"80", "443"});
        }

        String suffix = "." + this.deployment.getName() + "." + this.deployment.getDomain() + ".";

        String type = this.deployment.getGce().getMachine();

        this.sourceImage = "projects/centos-cloud/global/images/family/centos-7";

        if("ocp".equals(this.deployment.getType())) {
            this.sourceImage = "projects/rhel-cloud/global/images/family/rhel-7";
        }

        PersistentDisk pvsDisk = null;

        IPAddress address;

        address = new IPAddress(this.name + "-master", getZone());
        address.setProject(this.project);
        this.components.add(address);

        if(this.deployment.getComponents().getOrDefault("pvs", false)) {
            pvsDisk = new PersistentDisk(this.name + "-pvs", this.zone);
            this.components.add(pvsDisk);
            this.disks.add(pvsDisk);
            pvsDisk.setProject(this.project);
            pvsDisk.setSize(this.deployment.getDisks().getPvs());
        }

        {
            PersistentDisk rootDisk = new PersistentDisk(this.name + "-master-root", this.zone);
            this.components.add(rootDisk);
            this.disks.add(rootDisk);
            rootDisk.setProject(this.project);
            rootDisk.setSource(this.sourceImage);
            rootDisk.setBoot(true);
            rootDisk.setSize(this.deployment.getDisks().getRoot());

            PersistentDisk dockerDisk = new PersistentDisk(this.name + "-master-docker", this.zone);
            this.components.add(dockerDisk);
            this.disks.add(dockerDisk);
            dockerDisk.setProject(this.project);
            dockerDisk.setSize(this.deployment.getDisks().getDocker());

            Machine master = new Machine(this.name + "-master", type, this.zone, this.network);
            this.components.add(master);
            this.master = master;
            master.setProject(this.project);
            master.addTag("master");
            master.addDisk(rootDisk);
            master.addDisk(dockerDisk);
            master.setIPAddress(address);

            this.deployment.getSsh().getKeys().forEach(key -> {
                master.addSshKey(new SshKey("openshift", key));
            });

            if (!this.deployment.getNodes().getInfra()) {
                this.infra = master;
                master.addTag("infra");
                master.addTag("node");
                this.nodes.add(master);
                if(this.deployment.getComponents().getOrDefault("pvs", false)) {
                    master.addDisk(pvsDisk);
                }
            }
        }

        if(this.deployment.getNodes().getInfra()) {
            address = new IPAddress(this.name + "-infra", getZone());
            address.setProject(this.project);
            this.components.add(address);

            PersistentDisk rootDisk = new PersistentDisk(this.name + "-infra-root", this.zone);
            this.disks.add(rootDisk);
            this.components.add(rootDisk);
            rootDisk.setProject(this.project);
            rootDisk.setSource(this.sourceImage);
            rootDisk.setBoot(true);
            rootDisk.setSize(this.deployment.getDisks().getRoot());

            PersistentDisk dockerDisk = new PersistentDisk(this.name + "-infra-docker", this.zone);
            this.components.add(dockerDisk);
            this.disks.add(dockerDisk);
            dockerDisk.setProject(this.project);
            dockerDisk.setSize(this.deployment.getDisks().getDocker());

            Machine infra = new Machine(this.name + "-infra", type, this.zone, this.network);
            this.components.add(infra);
            this.infra = infra;
            this.nodes.add(infra);
            infra.setProject(this.project);
            infra.addTag("infra");
            infra.addTag("node");
            infra.addDisk(rootDisk);
            infra.addDisk(dockerDisk);
            infra.setIPAddress(address);

            if(this.deployment.getComponents().get("pvs")) {
                infra.addDisk(pvsDisk);
            }

            this.deployment.getSsh().getKeys().forEach(key -> {
                infra.addSshKey(new SshKey("openshift", key));
            });
        }

        if(this.deployment.getNodes().getCount() > 0) {
            LongStream.rangeClosed(1, this.deployment.getNodes().getCount()).forEach(id -> {
                String name = this.name + "-node-" + id;

                IPAddress addr = new IPAddress(name, getZone());
                addr.setProject(this.project);
                this.components.add(addr);

                PersistentDisk rootDisk = new PersistentDisk(name + "-root", this.zone);
                this.disks.add(rootDisk);
                this.components.add(rootDisk);
                rootDisk.setProject(this.project);
                rootDisk.setSource(this.sourceImage);
                rootDisk.setBoot(true);
                rootDisk.setSize(this.deployment.getDisks().getRoot());

                PersistentDisk dockerDisk = new PersistentDisk(name + "-docker", this.zone);
                this.disks.add(dockerDisk);
                this.components.add(dockerDisk);
                dockerDisk.setProject(this.project);
                dockerDisk.setSize(this.deployment.getDisks().getDocker());

                Machine node = new Machine(name, type, this.zone, this.network);
                this.nodes.add(node);
                this.components.add(node);
                node.setProject(this.project);
                node.addTag("node");
                node.addDisk(rootDisk);
                node.addDisk(dockerDisk);
                node.setIPAddress(addr);

                this.deployment.getSsh().getKeys().forEach(key -> {
                    node.addSshKey(new SshKey("openshift", key));
                });
            });
        }

        DomainName domainName;
        domainName = new DomainName();
        this.components.add(domainName);
        domainName.setProject(this.project);
        domainName.setZone(this.deployment.getGce().getDns());
        domainName.setTtl(300);
        domainName.setType("A");
        domainName.setMachine(this.master);
        domainName.setName("console" + suffix);

        domainName = new DomainName();
        this.components.add(domainName);
        domainName.setProject(this.project);
        domainName.setZone(this.deployment.getGce().getDns());
        domainName.setTtl(300);
        domainName.setType("A");
        domainName.setMachine(this.infra);
        domainName.setName("*.apps" + suffix);
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    public Zone getZone() {
        return zone;
    }

    public PrivateNetwork getNetwork() {
        return network;
    }

    public List<PersistentDisk> getDisks() {
        return disks;
    }

    public List<Machine> instances() {
        return nodes;
    }

    public Deployment getDeployment() {
        return this.deployment;
    }

    @Override
    public Instance instance(String label) {
        Optional<Machine> item = this.nodes.stream().filter(node -> {
            return node.getTags().contains(label);
        }).findFirst();

        if(item.isPresent()) {
            return item.get();
        } else {
            return null;
        }
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return this.nodes.stream().filter(node -> {
            return node.getTags().contains(label);
        }).collect(Collectors.toList());
    }

    public void setClient(GceClient client) {
        this.client = client;
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
