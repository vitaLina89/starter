package com.example.metricsstarter.aspect;

import com.example.metricsstarter.annotation.CountMetric;
import com.example.metricsstarter.annotation.TimeMetric;
import com.example.metricsstarter.annotation.SuccessErrorMetric;
import com.example.metricsstarter.metrics.MetricCollector;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final MetricCollector metricCollector;

    @Around("@annotation(countMetric)")
    public Object countMetrics(ProceedingJoinPoint joinPoint, CountMetric countMetric) throws Throwable {
        String metricName = countMetric.value().isEmpty() ? joinPoint.getSignature().toShortString() : countMetric.value();
        metricCollector.recordCount(metricName);
        return joinPoint.proceed();
    }

    @Around("@annotation(timeMetric)")
    public Object timeMetrics(ProceedingJoinPoint joinPoint, TimeMetric timeMetric) throws Throwable {
        String metricName = timeMetric.value().isEmpty() ? joinPoint.getSignature().toShortString() : timeMetric.value();
        Instant start = Instant.now();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = Duration.between(start, Instant.now()).toMillis();
            metricCollector.recordDuration(metricName, duration);
        }
    }

    @Around("@annotation(successErrorMetric)")
    public Object successErrorMetrics(ProceedingJoinPoint joinPoint, SuccessErrorMetric successErrorMetric) throws Throwable {
        String metricName = successErrorMetric.value().isEmpty() ? joinPoint.getSignature().toShortString() : successErrorMetric.value();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            metricCollector.recordSuccessError(metricName, success);
        }
    }
}
