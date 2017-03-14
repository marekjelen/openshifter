package eu.mjelen.openshifter.provider.gce.components;

public class FirewallAllow {

    private String protocol;
    private String[] ports;

    public FirewallAllow(String protocol, String[] ports) {
        this.protocol = protocol;
        this.ports = ports;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String[] getPorts() {
        return ports;
    }

    public void setPorts(String[] ports) {
        this.ports = ports;
    }

}
