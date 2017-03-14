package eu.mjelen.warden.api.cluster.map;

public class Zone {

    private final String name;

    private final Region region;

    public Zone(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }
}
