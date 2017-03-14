package eu.mjelen.openshifter.api;

import java.util.List;

public class Ssh {

    private List<String> keys;
    private String password;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
