package eu.mjelen.openshifter.api;

import java.util.LinkedList;
import java.util.List;

public class NodesNode {

    private String type;
    private List<NodesDisk> disks = new LinkedList<>();

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

}
