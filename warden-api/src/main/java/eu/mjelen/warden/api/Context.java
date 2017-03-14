package eu.mjelen.warden.api;

public interface Context {

    void setInjection(Class clazz, Object object);

    void setInjection(Object object);

    void skipChildTasks();

    void execute(Class task);

}
