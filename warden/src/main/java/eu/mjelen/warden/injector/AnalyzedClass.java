package eu.mjelen.warden.injector;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.*;

public class AnalyzedClass<T> implements Provider<T> {

    private final Injector injector;
    private final Class<T> clazz;
    private final boolean singleton;

    private T instance;
    private boolean bound;
    private T value;

    private InjectableConstructor<T> injectableConstructor = null;
    private Map<String, InjectableField> injectableFields = new HashMap<>();
    private Map<String, InjectableMethod> injectableMethods = new HashMap<>();

    public AnalyzedClass(Injector injector, Class<T> clazz) {
        this.injector = injector;
        this.clazz = clazz;

        this.singleton = this.clazz.isAnnotationPresent(Singleton.class);

        analyzeConstructors(clazz);
        analyzeFields(clazz, this.injectableFields);
        analyzeMethods(clazz, this.injectableMethods);
    }

    @Override
    public T get() {
        if(this.bound) {
            return this.value;
        }
        if(this.singleton) {
            LoggerFactory.getLogger(getClass()).debug("{} is singleton, reusing", this.clazz);
            if(this.instance == null) {
                this.instance = build();
            }
            return this.instance;
        }
        return build();
    }

    private T build() {
        LoggerFactory.getLogger(getClass()).debug("Building instance of {}", this.clazz);
        final T instance = this.injectableConstructor.inject(this.injector);

        if(this.injectableFields.size() > 0) {
            this.injectableFields.forEach((id, field) -> {
                field.inject(instance, this.injector);
            });
        }
        if(this.injectableMethods.size() > 0) {
            this.injectableMethods.forEach((id, method) -> {
                method.inject(instance, this.injector);
            });
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private void analyzeConstructors(Class clazz) {
        Arrays.asList(clazz.getConstructors()).forEach(constructor -> {
            if(constructor.isAnnotationPresent(Inject.class)) {
                this.injectableConstructor = new InjectableConstructor<>(this.clazz, constructor);
            }
            if(this.injectableConstructor == null && constructor.getParameterCount() == 0) {
                this.injectableConstructor = new InjectableConstructor<>(this.clazz, constructor);
            }
        });
    }

    private void analyzeFields(Class clazz, Map<String, InjectableField> fields) {
        Class parentClass = clazz.getSuperclass();
        if(parentClass != null) {
            analyzeFields(parentClass, fields);
        }
        Arrays.asList(clazz.getDeclaredFields()).forEach(field -> {
            if(field.isAnnotationPresent(Inject.class) && !Modifier.isFinal(field.getModifiers())) {
                String id = clazz.getName() + "-" + field.getName();
                fields.put(id, new InjectableField(clazz, field));
            }
        });
    }

    private void analyzeMethods(Class clazz, Map<String, InjectableMethod> methods) {
        Class parentClass = clazz.getSuperclass();
        if(parentClass != null) {
            analyzeMethods(parentClass, methods);
        }
        Arrays.asList(clazz.getDeclaredMethods()).forEach(method -> {
            String id = method.getName();
            if(method.isAnnotationPresent(Inject.class) && !Modifier.isAbstract(method.getModifiers())) {
                methods.put(id, new InjectableMethod(clazz, method));
            } else if(methods.containsKey(id)) {
                methods.remove(id);
            }
        });
    }

    @Override
    public String toString() {
        return "AnalyzedClass{" +
                "clazz=" + clazz +
                ", injectableConstructor=" + injectableConstructor +
                ", injectableFields=" + injectableFields +
                ", injectableMethods=" + injectableMethods +
                '}';
    }

    public void bind(T value) {
        this.bound = true;
        this.value = value;
    }

    public void unbind() {
        this.bound = false;
        this.value = null;
    }
}
