package eu.mjelen.openshifter.api;

import java.util.List;
import java.util.Map;

public class Byo {

    private Map<String, List<String>> servers;

    public Map<String, List<String>> getServers() {
        return servers;
    }

    public void setServers(Map<String, List<String>> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "Byo{" +
                "servers=" + servers +
                '}';
    }
}
