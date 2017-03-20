package eu.mjelen.openshifter.api;

public class AWS {

    private String account;
    private String zone = "us-west-1a";
    private String region = "us-west-1";
    private String machine = "m3.medium";
    private String project;
    private String dns;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getRegion() {
        if(this.region == null && this.zone != null) {
            int pos = this.zone.length()-1;
            this.region = this.zone.substring(0, pos);
        }
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    @Override
    public String toString() {
        return "AWS{" +
                "account='" + account + '\'' +
                ", zone='" + zone + '\'' +
                ", region='" + getRegion() + '\'' +
                ", machine='" + machine + '\'' +
                ", project='" + project + '\'' +
                ", dns='" + dns + '\'' +
                '}';
    }
}
