package com.example.library.annotation.parameter;

import com.example.library.util.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface HttpTrigger {
    HttpMethod httpMethod() default HttpMethod.GET;
    KeyValue[] keyValueArray() default {};
    String authentication() default "";
    String data() default "";
    Class dataType() default Void.class;
    Class returnType() default Void.class;
}
