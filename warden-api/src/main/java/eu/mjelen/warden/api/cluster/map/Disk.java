package eu.mjelen.warden.api.cluster.map;

public class Disk {

    private String name;

    private String type;

    private boolean boot;
    private String source;
    private Long size;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBoot(boolean boot) {
        this.boot = boot;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isBoot() {
        return boot;
    }

    public String getSource() {
        return source;
    }

    public Long getSize() {
        return size;
    }
}
