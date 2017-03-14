package eu.mjelen.openshifter.provider.gce.components;

import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.Operation;
import eu.mjelen.openshifter.provider.gce.GceComponent;

import java.io.IOException;

public class PersistentDisk extends GceComponent {

    private String name;

    private Zone zone;
    private String type;

    private Boolean boot = false;
    private String source;
    private Long size;

    public PersistentDisk(String name, Zone zone) {
        this.name = name;
        this.zone = zone;
        this.type = "zones/" + this.zone.getZone() + "/diskTypes/pd-ssd";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getBoot() {
        return boot;
    }

    public void setBoot(Boolean boot) {
        this.boot = boot;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Operation doLoad() throws IOException {
        Disk disk = this.compute().disks()
                .get(this.getProject().getName(), this.getZone().getZone(), this.getName()).execute();
        setUrl(disk.getSelfLink());
        return null;
    }

    @Override
    public Operation doCreate() throws Exception {
        Disk disk = new Disk();
        disk.setName(getName());
        disk.setType(getType());

        if(getSize() != null) {
            disk.setSizeGb(getSize());
        }

        if(getSource() != null) {
            disk.setSourceImage(getSource());
        }

        return compute().disks().insert(getProject().getName(), getZone().getZone(), disk).execute();
    }

    @Override
    public Operation doDestroy() throws Exception {
        return compute().disks().delete(getProject().getName(), getZone().getZone(), getName()).execute();
    }
}
