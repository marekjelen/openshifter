package eu.mjelen.openshifter.api;

import eu.mjelen.warden.api.Descriptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deployment implements Descriptor {

    private String provider;
    private String installer = "ansible";
    private String name;
    private String type = "origin";
    private String release = "v1.4.0";
    private Dns dns = new Dns();
    private Map<String, Boolean> components = new HashMap<>();
    private List<String> templates = Collections.emptyList();
    private List<String> execute = Collections.emptyList();
    private Docker docker = new Docker();
    private PVs pvs = new PVs();
    private Nodes nodes = new Nodes();
    private Disks disks = new Disks();
    private Ssh ssh = new Ssh();
    private Users users = new Users();

    private GCE gce;
    private AWS aws;
    private Linode linode;
    private Byo byo;

    @Override
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public Dns getDns() {
        return dns;
    }

    public void setDns(Dns dns) {
        this.dns = dns;
    }

    public Map<String, Boolean> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Boolean> components) {
        this.components = components;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public List<String> getExecute() {
        return execute;
    }

    public void setExecute(List<String> execute) {
        this.execute = execute;
    }

    public Docker getDocker() {
        return docker;
    }

    public void setDocker(Docker docker) {
        this.docker = docker;
    }

    public PVs getPvs() {
        return pvs;
    }

    public void setPvs(PVs pvs) {
        this.pvs = pvs;
    }

    public Nodes getNodes() {
        return nodes;
    }

    public void setNodes(Nodes nodes) {
        this.nodes = nodes;
    }

    public Disks getDisks() {
        return disks;
    }

    public void setDisks(Disks disks) {
        this.disks = disks;
    }

    public Ssh getSsh() {
        return ssh;
    }

    public void setSsh(Ssh ssh) {
        this.ssh = ssh;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public GCE getGce() {
        return gce;
    }

    public void setGce(GCE gce) {
        this.gce = gce;
    }

    public AWS getAws() {
        return aws;
    }

    public void setAws(AWS aws) {
        this.aws = aws;
    }


    public Linode getLinode() {
        return linode;
    }

    public void setLinode(Linode linode) {
        this.linode = linode;
    }

    public Byo getByo() {
        return byo;
    }

    public void setByo(Byo byo) {
        this.byo = byo;
    }

}
