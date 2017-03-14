package eu.mjelen.openshifter.provider.gce.components;

public class Zone extends Region {

    private String zone;

    public Zone(String region, String zone) {
        super(region);
        this.zone = zone;
    }

    public Zone(Region region, String zone) {
        this(region.getRegion(), zone);
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

}
