package eu.mjelen.openshifter.api;

import java.util.Collections;
import java.util.List;

public class UsersRandom {

    private String username;
    private String password;

    private int min = 0;
    private int max = 0;

    private List<String> execute = Collections.emptyList();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<String> getExecute() {
        return execute;
    }

    public void setExecute(List<String> execute) {
        this.execute = execute;
    }
}
