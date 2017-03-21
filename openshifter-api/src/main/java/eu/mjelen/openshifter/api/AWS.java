package eu.mjelen.openshifter.api;

public class AWS {

    private String key;
    private String secret;
    private Long count = 0L;
    private String type = "t2.large";
    private String zone = "us-west-1a";
    private String region = "us-west-1";


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
    
    public String getZone() {
        return zone;
    }

    public String getRegion() {
        int pos = this.zone.length()-1;
        this.region = this.zone.substring(0, pos);
        return this.region;
    }
}
