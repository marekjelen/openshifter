package eu.mjelen.openshifter.provider.gce.template;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.tool.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

public class GceTemplate {

    private Map<String, GceTemplateResource> resources = new LinkedHashMap<>();

    public GceTemplate(Deployment deployment) {
        add(deployment.getName(), "compute.v1.network", Maps.dict().put("autoCreateSubnetworks", true).build());

        add("all", "compute.v1.firewall", Maps.dict().build());
    }

    public GceTemplate add(String name, String type, Map<String, Object> properties) {
        add(new GceTemplateResource(name, type, properties));
        return this;
    }

    public GceTemplate add(GceTemplateResource resource) {
        this.resources.put(resource.getName(), resource);
        return this;
    }

}
