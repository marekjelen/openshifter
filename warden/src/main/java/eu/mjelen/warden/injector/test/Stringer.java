package eu.mjelen.warden.injector.test;

import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Stringer implements Provider<String> {

    private int i = 0;
    @Override
    public String get() {
        this.i += 1;
        return "Hello " + this.i;
    }

}
