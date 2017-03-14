package eu.mjelen.warden.api.cluster.map;

public class DnsRecord {

    private String name;
    private String type;
    private Integer ttl;

    private String machine;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getTtl() {
        return ttl;
    }

    public String getMachine() {
        return machine;
    }

}
