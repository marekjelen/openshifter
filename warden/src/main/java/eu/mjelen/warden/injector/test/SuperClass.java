package eu.mjelen.warden.injector.test;

import javax.inject.Inject;
import javax.inject.Provider;

public class SuperClass {

    private final String system;

    @Inject
    private String name;

    @Inject
    private Provider<String> stringProvider;

    @Inject
    public SuperClass(String system) {
        this.system = system;
    }

    @Inject
    public void publicInjector(Provider<String> stringer) {
        System.out.println("public " + stringer.get());
    }

    @Inject
    private void privateInjector() {
        System.out.println("private " + stringProvider.get());
    }

    @Inject
    private void hiddenInjector() {
        System.out.println("not called");
    }

    @Override
    public String toString() {
        return "SuperClass{" +
                "system='" + system + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
