package eu.mjelen.openshifter.provider.gce.components;

import com.google.api.services.compute.model.Address;
import com.google.api.services.compute.model.Operation;
import eu.mjelen.openshifter.provider.gce.GceComponent;

import java.io.IOException;

public class IPAddress extends GceComponent {

    private String name;

    private Region region;
    private String address;

    public IPAddress(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Operation doLoad() throws IOException {
        Address address = compute().addresses()
                .get(this.getProject().getName(), this.getRegion().getRegion(), this.getName()).execute();
        setUrl(address.getSelfLink());
        this.address = address.getAddress();
        return null;
    }

    @Override
    public Operation doCreate() throws Exception {
        Address address = new Address();
        address.setName(getName());
        return compute().addresses().insert(getProject().getName(), getRegion().getRegion(), address).execute();
    }

    @Override
    public Operation doDestroy() throws Exception {
        return compute().addresses().delete(getProject().getName(), getRegion().getRegion(), getName()).execute();
    }
}
