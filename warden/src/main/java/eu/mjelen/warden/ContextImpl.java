package eu.mjelen.warden;

import eu.mjelen.warden.api.Connections;
import eu.mjelen.warden.api.Context;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.injector.Injector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContextImpl implements Context {

    private final Injector injector = new Injector();
    private final Map<String, Object> values = new HashMap<>();
    private final Cluster cluster;
    private final Connections connections;

    private List<Class> executions;
    private boolean skipChildTasks;

    public ContextImpl(Cluster cluster, Connections connections) {
        this.cluster = cluster;
        this.setInjection(Cluster.class, cluster);
        this.connections = connections;
    }

    @Override
    public void setInjection(Class clazz, Object object) {
        this.injector.analyze(clazz);
        this.injector.bind(clazz, object);
    }

    @Override
    public void setInjection(Object object) {
        setInjection(object.getClass(), object);
    }

    public Object getInjection(Class clazz) {
        if(clazz == Context.class) {
            return this;
        }
        return this.injector.get(clazz);
    }

    public void put(String name, Object value) {
        this.values.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <A> A get(String name, Class<A> clazz) {
        return (A) this.values.get(name);
    }

    public Object get(String name) {
        return this.values.get(name);
    }

    public void clean() {
        this.skipChildTasks = false;
        this.executions = new LinkedList<>();
    }

    @Override
    public void execute(Class task) {
        this.skipChildTasks();
        this.executions.add(task);
    }

    public List<Class> getExecutions() {
        return this.executions;
    }

    @Override
    public void skipChildTasks() {
        this.skipChildTasks = true;
    }

    public boolean isSkipChildTasks() {
        return skipChildTasks;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public Connections getConnections() {
        return connections;
    }

}
