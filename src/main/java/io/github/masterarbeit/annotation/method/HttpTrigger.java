package io.github.masterarbeit.annotation.method;

import io.github.masterarbeit.util.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpTrigger {
    HttpMethod httpMethod() default HttpMethod.GET;

    String authentication() default "";
}

