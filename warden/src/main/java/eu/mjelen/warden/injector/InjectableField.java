package eu.mjelen.warden.injector;

import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;

public class InjectableField<T> extends Injectable {

    private final Class<T> clazz;
    private final Field field;
    private final Class[] types;
    private final Type[] generics;

    private MethodHandle handle;

    public InjectableField(Class<T> clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
        this.field.setAccessible(true);

        this.types = new Class[]{ this.field.getType() };
        this.generics = new Type[] { this.field.getGenericType() };

        try {
            if(Modifier.isPublic(this.field.getModifiers())) {
                this.handle = MethodHandles.lookup().findSetter(clazz, field.getName(), this.field.getType());
            } else {
                this.handle = MethodHandles.lookup().unreflectSetter(field);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LoggerFactory.getLogger(getClass()).error("Failed acquiring method handle", e);
        }
    }

    public Object inject(Object instance, Injector injector) {
        Object[] arguments = arguments(this.types, this.generics, injector);

        try {
            this.handle.invokeWithArguments(Arrays.asList(instance, arguments[0]));
            return instance;
        } catch (Throwable e) {
            LoggerFactory.getLogger(getClass()).error("Failed invoking method handle", e);
        }

        return instance;
    }

}
