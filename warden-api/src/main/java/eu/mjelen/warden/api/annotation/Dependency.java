package eu.mjelen.warden.api.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Dependencies.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {
    Class value();
}
