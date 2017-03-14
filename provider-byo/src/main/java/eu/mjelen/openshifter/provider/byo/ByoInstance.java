package eu.mjelen.openshifter.provider.byo;

import eu.mjelen.warden.api.cluster.Instance;

import java.util.LinkedList;
import java.util.List;

public class ByoInstance implements Instance {

    private final String ip;
    private final List<String> tags = new LinkedList<>();

    public ByoInstance(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public void load() {
        // not required
    }

    @Override
    public void create() {
        // not required
    }

    @Override
    public void destroy() {
        // not required
    }

    @Override
    public String getAddress() {
        return this.ip;
    }

    @Override
    public String getInternalAddress() {
        return this.ip;
    }

    @Override
    public List<String> getTags() {
        return this.tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    @Override
    public String toString() {
        return "ByoInstance{" +
                "ip='" + ip + '\'' +
                ", tags=" + tags +
                '}';
    }
}
