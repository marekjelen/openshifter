package eu.mjelen.warden.injector;

import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;

public class InjectableConstructor<T> extends Injectable {

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Class<?>[] types;
    private final Type[] generics;
    private MethodHandle handle;

    public InjectableConstructor(Class<T> clazz, Constructor<T> constructor) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.types = this.constructor.getParameterTypes();
        this.generics = this.constructor.getGenericParameterTypes();

        try {
            if(Modifier.isPublic(constructor.getModifiers())) {
                this.handle = MethodHandles.lookup().findConstructor(clazz,
                    MethodType.methodType(void.class, this.types));
            } else {
                this.handle = MethodHandles.lookup().unreflectConstructor(constructor);
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LoggerFactory.getLogger(getClass()).error("Failed acquiring method handle", e);
        }
    }

    @SuppressWarnings("unchecked")
    public T inject(Injector injector) {
        Object[] arguments = arguments(this.types, this.generics, injector);
        try {
            return (T) this.handle.invokeWithArguments(Arrays.asList(arguments));
        } catch (Throwable e) {
            LoggerFactory.getLogger(getClass()).error("Failed invoking method handle", e);
        }
        return null;
    }
}
