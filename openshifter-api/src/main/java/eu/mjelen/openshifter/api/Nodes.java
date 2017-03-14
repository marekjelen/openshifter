package eu.mjelen.openshifter.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Nodes {

    private Long count = 0L;
    private Boolean infra = false;
    private String region;
    private String zone;
    private String type;

    private List<NodesDisk> disks = new LinkedList<>();
    private Map<String, NodesNode> nodes = new HashMap<>();

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Boolean getInfra() {
        return infra;
    }

    public void setInfra(Boolean infra) {
        this.infra = infra;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<NodesDisk> getDisks() {
        return disks;
    }

    public void setDisks(List<NodesDisk> disks) {
        this.disks = disks;
    }

    public Map<String, NodesNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, NodesNode> nodes) {
        this.nodes = nodes;
    }

}
