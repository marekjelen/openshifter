package eu.mjelen.openshifter.api;

import java.util.Collections;
import java.util.List;

public class Docker {

    private List<String> prime = Collections.emptyList();

    public List<String> getPrime() {
        return prime;
    }

    public void setPrime(List<String> prime) {
        this.prime = prime;
    }
}
