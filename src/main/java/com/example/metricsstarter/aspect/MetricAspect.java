package com.example.metricsstarter.aspect;

import com.example.metricsstarter.annotation.Metric;
import com.example.metricsstarter.metrics.MetricCollector;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {


    private final MetricCollector metricCollector;

    @Pointcut("@annotation(com.example.metricsstarter.annotation.Metric)")
    public void metricAnnotatedMethod() {}

    @Around("metricAnnotatedMethod() && @annotation(metric)")
    public Object collectMetrics(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        String metricName = metric.value().isEmpty() ? joinPoint.getSignature().toShortString() : metric.value();
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;
            metricCollector.recordMethodCall(metricName, duration, success);
        }
    }
}
