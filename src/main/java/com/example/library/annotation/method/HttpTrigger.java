package com.example.library.annotation.method;

import com.example.library.util.HttpMethod;

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

