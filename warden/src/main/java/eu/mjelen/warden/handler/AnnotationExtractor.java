package eu.mjelen.warden.handler;

import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AnnotationExtractor {

    private final List<Class> annotations;

    public AnnotationExtractor(Class... annotations) {
        this.annotations = Arrays.asList(annotations);
    }

    @SuppressWarnings("unchecked")
    public <A> List<A> get(String method, Class clazz) {
        List<A> values = new LinkedList<>();

        this.annotations.forEach(annotation -> {
            try {
                Method mth = annotation.getMethod(method);
                Annotation a = clazz.getAnnotation(annotation);
                if(a != null) {
                    Object value = mth.invoke(a);
                    if(value instanceof Object[]) {
                        for(Object item : (Object[]) value) {
                            Method m = item.getClass().getMethod(method);
                            values.add((A) m.invoke(item));
                        }
                    } else {
                        values.add((A) value);
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                // skip
            }
        });
        LoggerFactory.getLogger(getClass()).info("{}", values);
        return values;
    }

}
