package eu.mjelen.warden.injector.test;

import javax.inject.Inject;

public class SubClass extends SuperClass{

    @Inject
    private String name;

    @Inject
    public SubClass(String system) {
        super(system);
    }

    public void hiddenInjector() {

    }

    @Override
    public String toString() {
        return "SubClass{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
