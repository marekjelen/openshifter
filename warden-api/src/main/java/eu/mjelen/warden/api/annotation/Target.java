package eu.mjelen.warden.api.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Targets.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Target {
    String value();
}
