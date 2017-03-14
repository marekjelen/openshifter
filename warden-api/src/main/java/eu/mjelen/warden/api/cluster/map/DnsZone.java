package eu.mjelen.warden.api.cluster.map;

import java.util.HashSet;
import java.util.Set;

public class DnsZone {

    private String name;

    private Set<DnsRecord> records = new HashSet<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addRecord(DnsRecord record) {
        this.records.add(record);
    }

    public String getName() {
        return name;
    }

    public Set<DnsRecord> getRecords() {
        return records;
    }
}
