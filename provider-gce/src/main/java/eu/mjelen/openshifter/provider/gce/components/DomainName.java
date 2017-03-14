package eu.mjelen.openshifter.provider.gce.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.dns.model.Change;
import com.google.api.services.dns.model.ResourceRecordSet;
import com.google.api.services.dns.model.ResourceRecordSetsListResponse;
import eu.mjelen.openshifter.provider.gce.GceComponent;
import eu.mjelen.openshifter.provider.gce.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DomainName extends GceComponent {

    private String name;

    private String zone;

    private String type;
    private Integer ttl;
    private Machine machine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    @Override
    public Operation doLoad() throws Exception {
        ResourceRecordSetsListResponse rrss = dns().resourceRecordSets().list(getProject().getName(), getZone()).execute();
        List<String> names = rrss.getRrsets().stream().map(ResourceRecordSet::getName).collect(Collectors.toList());
        if(!names.contains(getName())) {
            throw new NotFoundException();
        }
        return null;
    }

    @Override
    public Operation doCreate() throws Exception {
        Change change = new Change();
        change.setAdditions(Collections.singletonList(getRrs()));
        dns().changes().create(getProject().getName(), getZone(), change).execute();
        return null;
    }

    @Override
    public Operation doDestroy() throws Exception {
        Change change = new Change();
        change.setDeletions(Collections.singletonList(getRrs()));
        dns().changes().create(getProject().getName(), getZone(), change).execute();
        return null;
    }

    @JsonIgnore
    public ResourceRecordSet getRrs() {
        ResourceRecordSet rrs = new ResourceRecordSet();
        rrs.setName(getName());
        rrs.setType(getType());
        rrs.setTtl(getTtl());
        rrs.setRrdatas(Collections.singletonList(this.machine.getAddress()));
        return rrs;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }
}
