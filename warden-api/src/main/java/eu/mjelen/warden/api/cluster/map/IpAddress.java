package eu.mjelen.warden.api.cluster.map;

public class IpAddress {

    private String name;
    private String address;

    private boolean internal;
    private boolean ephemeral;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }
}
