package eu.mjelen.warden.injector;

import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class InjectableMethod<T> extends Injectable {

    private final Class<T> clazz;
    private final Method method;
    private final Class<?>[] types;
    private final Type[] generics;
    private final Class<?> r;

    private MethodHandle handle;

    public InjectableMethod(Class<T> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
        this.method.setAccessible(true);

        this.types = method.getParameterTypes();
        this.generics = method.getGenericParameterTypes();
        this.r = method.getReturnType();

        try {
            if(Modifier.isPublic(method.getModifiers())) {
                this.handle = MethodHandles.lookup().findVirtual(clazz, method.getName(),
                        MethodType.methodType(this.r, this.types));
            } else {
                this.handle = MethodHandles.lookup().unreflect(method);
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LoggerFactory.getLogger(getClass()).error("Failed acquiring method handle", e);
        }
    }

    public Object inject(Object instance, Injector injector) {
        Object[] arguments = arguments(this.types, this.generics, injector);
        try {
            List<Object> args = new LinkedList<>();
            args.add(instance);
            args.addAll(Arrays.asList(arguments));
            this.handle.invokeWithArguments(args);
            return instance;
        } catch (Throwable e) {
            LoggerFactory.getLogger(getClass()).error("Failed invoking method handle", e);
        }
        return instance;
    }
}
