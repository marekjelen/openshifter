package eu.mjelen.warden.injector.test;

import eu.mjelen.warden.injector.Injector;

public class Main {

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        Injector injector = new Injector();
        injector.analyze(Stringer.class);
        injector.analyze(SubClass.class);

        System.out.println(injector.get(SubClass.class));
    }
}
