package eu.mjelen.openshifter.api;

public class Linode {

    private String key;
    private String datacenter = "dallas";
    private Long plan = 8192L;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public Long getPlan() {
        return plan;
    }

    public void setPlan(Long plan) {
        this.plan = plan;
    }
}
