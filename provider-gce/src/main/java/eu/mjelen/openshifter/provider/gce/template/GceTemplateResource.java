package eu.mjelen.openshifter.provider.gce.template;

import java.util.Map;

public class GceTemplateResource {

    private String name;
    private String type;
    private Map<String, Object> properties;

    public GceTemplateResource(String name, String type, Map<String, Object> properties) {
        this.name = name;
        this.type = type;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
