package eu.mjelen.warden.api;

public interface Component {

    boolean exists();

    void load();

    void create();

    void destroy();

}
