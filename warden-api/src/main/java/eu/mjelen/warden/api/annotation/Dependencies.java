package eu.mjelen.warden.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {

    Dependency[] value() default {};

}
