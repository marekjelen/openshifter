package eu.mjelen.warden.handler;

import eu.mjelen.warden.api.annotation.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExecutorFinder {

    public interface ExecutorRunner {
        Object execute(Object instance, Object... args);
    }

    private final Class clazz;
    private final Logger logger;

    private int parameters = 0;
    private Class<? extends Annotation> marker = Executor.class;

    public ExecutorFinder(Class clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger("ExecutorFinder [" + clazz.getName() + "]");
    }

    public void setParameters(int parameters) {
        this.parameters = parameters;
    }

    public void setMarker(Class<? extends Annotation> marker) {
        this.marker = marker;
    }

    public ExecutorRunner find() {
        this.logger.debug("Determining task executor");

        List<Method> methods = Arrays.asList(this.clazz.getMethods());

        List<Method> locals = methods.stream().filter(method -> {
            return Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == this.parameters;
        }).filter(method -> {
            return method.getDeclaringClass() == clazz;
        }).collect(Collectors.toList());

        if(locals.size() == 1) {
            this.logger.debug("Single public method executor");
            return (instance, args) -> {
                try {
                    return locals.get(0).invoke(instance, args);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            };
        }

        Optional<Method> executor = methods.stream().filter(method -> {
            return Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0;
        }).filter(method -> {
            return method.getAnnotation(this.marker) != null;
        }).findFirst();

        if(executor.isPresent()) {
            this.logger.debug("Method marked as Executor");
            return (instance, args) -> {
                try {
                    return executor.get().invoke(instance, args);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            };
        }

        this.logger.debug("Can not determine executor");
        return null;
    }


}
