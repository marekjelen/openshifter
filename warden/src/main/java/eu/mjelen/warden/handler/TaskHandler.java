package eu.mjelen.warden.handler;

import eu.mjelen.warden.ContextImpl;
import eu.mjelen.warden.api.Context;
import eu.mjelen.warden.api.annotation.*;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.*;

public class TaskHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Class clazz;
    private ExecutorFinder.ExecutorRunner executor;
    private Map<String, FieldInjection> injections = new HashMap<>();
    private List<Class> dependencies = new LinkedList<>();
    private Class parent = null;
    private Map<Class, TaskHandler> children = new HashMap<>();
    private List<Class> taskOrder = new LinkedList<>();
    private List<String> targets = new LinkedList<>();

    public TaskHandler() {
    }

    public TaskHandler(Class clazz) {
        analyze(clazz);
    }

    public TaskHandler analyze(Class clazz) {
        this.logger.debug("Analyzing {}", clazz);

        this.clazz = clazz;

        this.executor = new ExecutorFinder(this.clazz).find();

        AnnotationExtractor dependencyExtractor = new AnnotationExtractor(Dependency.class, Dependencies.class);
        this.dependencies = dependencyExtractor.get("value", clazz);

        AnnotationExtractor targetExtractor = new AnnotationExtractor(Target.class, Targets.class);
        this.targets = targetExtractor.get("value", clazz);

        Field[] fields = this.clazz.getDeclaredFields();
        for(Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if(inject == null) continue;
            this.injections.put(field.getName(), new FieldInjection(field));
        }

        Task task = (Task) clazz.getAnnotation(Task.class);
        if(task.parent() != Object.class) {
            this.parent = task.parent();
        }

        return this;
    }

    public List<Class> getDependencies() {
        return dependencies;
    }

    public Class getParent() {
        return parent;
    }

    public Class getTaskClass() {
        return clazz;
    }

    public void execute(ContextImpl context) {
        if(this.targets.size() == 0) {
            executeWithConnection(context, null);
        } else {
            this.targets.forEach(target -> {
                context.getConnections().getConnections(target).forEach(connection -> {
                    executeWithConnection(context, connection);
                });
            });
        }
    }

    public void executeWithConnection(ContextImpl context, Connection connection) {
        try {
            String name;
            if(connection != null) {
                name = this.clazz.getSimpleName() + " [" + connection.getHost() + "]";
            } else {
                name = this.clazz.getSimpleName();
            }
            Logger logger = LoggerFactory.getLogger(name);

            context.clean();
            context.setInjection(Connection.class, connection);
            context.setInjection(Logger.class, logger);

            if(this.executor != null) {
                Object instance = clazz.newInstance();
                this.injections.forEach((field, injection) -> {
                    Object value = context.getInjection(injection.getType());
                    injection.inject(instance, value);
                });
                this.executor.execute(instance);
            }

            List<Class> executions = this.taskOrder;
            if(context.isSkipChildTasks()) {
                executions = context.getExecutions();
            }
            executions.forEach(clazz -> {
                TaskHandler task = this.children.get(clazz);
                task.execute(context);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void addChild(TaskHandler task) {
        this.children.put(task.getTaskClass(), task);
        this.taskOrder.add(task.getTaskClass());
        sort();
    }

    private void sort() {
        boolean changed = true;
        while(changed) {
            changed = false;
            for(int i = 0; i < this.taskOrder.size(); i++) {
                Class task = this.taskOrder.get(i);
                TaskHandler handler = this.children.get(task);
                for(Class dep : handler.getDependencies()) {
                    int di = this.taskOrder.indexOf(dep);
                    if(di > i) {
                        this.taskOrder.set(di, task);
                        this.taskOrder.set(i, dep);
                        changed = true;
                    }
                }
            }
        }
    }
}
