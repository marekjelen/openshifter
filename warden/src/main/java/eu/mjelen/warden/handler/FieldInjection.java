package eu.mjelen.warden.handler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public class FieldInjection {

    private final String name;
    private final Class type;
    private final Class target;

    private BiConsumer executor;
    private Field field;
    private MethodHandle handle;

    public FieldInjection(Field field) {
        this.name = field.getName();
        this.type = field.getType();
        this.target = field.getDeclaringClass();
        this.field = field;

        try {
            if(Modifier.isPublic(this.field.getModifiers())) {
                this.handle = MethodHandles.lookup().findSetter(target, name, type);
                this.executor = (instance, value) -> {
                    try {
                        this.handle.invoke(instance, value);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                };
            } else {
                this.field.setAccessible(true);
                this.executor = (instance, value) -> {
                    try {
                        this.field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                };
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Class getType() {
        return type;
    }

    public void inject(Object instance, Object value) {
        this.executor.accept(instance, value);
    }
}
