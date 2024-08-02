package com.example.metricsstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation for counting method calls
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountMetric {
    String value() default "";
}
