package eu.mjelen.openshifter.cli;

import java.util.Map;

public class Arguments {

    private final Map<String, Object> attrs;

    public Arguments(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public boolean hasFlag(String name) {
        return this.attrs.get(name) != null && (boolean) this.attrs.get(name);
    }

    public String getAction() {
        return (String) this.attrs.get("action");
    }

    public String getName() {
        return (String) this.attrs.get("name");
    }

}
