package eu.mjelen.openshifter.api;

public class NodesDisk {

    private Long size = 100L;
    private Boolean boot = false;
    private String type = "ssd";

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getBoot() {
        return boot;
    }

    public void setBoot(Boolean boot) {
        this.boot = boot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
