package eu.mjelen.openshifter.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Users {

    private Map<String, String> admin = Collections.emptyMap();
    private List<UsersRegular> regular = Collections.emptyList();
    private UsersRandom random;

    public Map<String, String> getAdmin() {
        return admin;
    }

    public void setAdmin(Map<String, String> admin) {
        this.admin = admin;
    }

    public List<UsersRegular> getRegular() {
        return regular;
    }

    public void setRegular(List<UsersRegular> regular) {
        this.regular = regular;
    }

    public UsersRandom getRandom() {
        return random;
    }

    public void setRandom(UsersRandom random) {
        this.random = random;
    }

}
