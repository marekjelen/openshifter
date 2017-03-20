package eu.mjelen.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.mjelen.warden.api.ContextBuilder;
import eu.mjelen.warden.api.Descriptor;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import eu.mjelen.warden.handler.RootTask;
import eu.mjelen.warden.handler.TaskHandler;
import eu.mjelen.warden.provider.ProviderHolder;
import eu.mjelen.warden.ssh.SshConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Warden<A extends Descriptor> {

    private static final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper json = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<A> descriptorClass;
    private Index index;

    private TaskHandler rootTask = new TaskHandler(RootTask.class);

    private ContextBuilder contextBuilder;
    private Map<Class, TaskHandler> tasks = new HashMap<>();
    private Map<String, ProviderHolder<A>> providers = new HashMap<>();
    private A descriptor;
    private Cluster cluster;
    private SshConnections connections;

    public Warden(Class<A> descriptorClass) {
        this.logger.info("Initializing container");

        this.descriptorClass = descriptorClass;

        try {
            this.index = new Index();
            this.index.getClassesWithAnnotation(Task.class).forEach(task -> {
                this.inject(task);
            });
        } catch (ClassNotFoundException e) {
            this.logger.info("Manual injection required, scanner not found");
        }
    }

    public void setContextBuilder(ContextBuilder contextBuilder) {
        this.contextBuilder = contextBuilder;
    }

    public void loadFromObject(A descriptor) {
        this.descriptor = descriptor;
    }

    public void loadFromFile(String file) throws IOException {
        this.logger.debug("Loading descriptor from file");
        if(file.endsWith(".yml")) {
            loadFromObject(yaml.readValue(new FileInputStream(file), this.descriptorClass));
        } else {
            loadFromObject(json.readValue(new FileInputStream(file), this.descriptorClass));
        }
    }

    public A getDescriptor() {
        return descriptor;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void validateCluster() {
        this.logger.debug("Generating plan for provider");

        this.cluster = this.getProvider(this.descriptor.getProvider()).analyze(this.descriptor);
        if(this.cluster == null) throw new NullPointerException("Cluster map can not be null");

        this.cluster.validate();
    }

    public void buildCluster() {
        this.logger.info("Creating cluster");
        this.cluster.create();
        this.logger.info("Cluster creation finished");
    }

    public void destroyCluster() {
        this.logger.info("Destroying cluster");
        this.cluster.destroy();
        this.logger.info("Cluster destruction finished");
    }

    @SuppressWarnings("unchecked")
    public void inject(Class clazz) {
        if(clazz.getAnnotation(Task.class) != null) {
            injectTask(clazz);
        }
    }

    public void inject(String name, ClusterProvider<A> clusterProvider) {
        this.providers.put(name, new ProviderHolder<A>(clusterProvider));
    }

    private void injectTask(Class clazz) {
        this.tasks.putIfAbsent(clazz, new TaskHandler());
        TaskHandler task = this.tasks.get(clazz);
        task.analyze(clazz);

        if(task.getParent() != null) {
            this.tasks.put(clazz, task);
            this.tasks.putIfAbsent(task.getParent(), new TaskHandler());
            this.tasks.get(task.getParent()).addChild(task);
        } else {
            this.tasks.put(clazz, task);
            this.rootTask.addChild(task);
        }
    }

    public void execute() {
        this.logger.info("Configuring SSH connections");
        if(this.connections == null) {
            this.connections = new SshConnections(this.cluster);
        }
        ContextImpl context = new ContextImpl(getCluster(), this.connections);
        context.setInjection(this.descriptorClass, this.descriptor);
        context.setInjection(Logger.class, this.logger);

        if(this.contextBuilder != null) {
            this.contextBuilder.buildContext(context);
        }

        this.logger.info("Executing tasks: {}", this.tasks);

        this.rootTask.execute(context);
    }

    @Override
    public String toString() {
        return "Warden{" +
                "providers=" + providers +
                "tasks=" + tasks +
                '}';
    }

    public ProviderHolder getProvider(String provider) {
        if(!this.providers.containsKey(provider)) {
            this.providers.put(provider, new ProviderHolder<>(provider));
        }
        return this.providers.get(provider);
    }

}
