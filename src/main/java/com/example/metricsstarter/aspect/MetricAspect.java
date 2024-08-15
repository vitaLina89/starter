package com.example.metricsstarter.aspect;

import com.example.metricsstarter.annotation.CountMetric;
import com.example.metricsstarter.annotation.ErrorMetric;
import com.example.metricsstarter.annotation.SuccessMetric;
import com.example.metricsstarter.annotation.TimeMetric;
import com.example.metricsstarter.interfaceabstract.MetricCollectorInterface;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {


    private final MetricCollectorInterface metricCollector;

    @Before("@annotation(countMetric)")
    public void countMetrics(JoinPoint joinPoint, CountMetric countMetric) {
        String metricName = countMetric.value().isEmpty() ? joinPoint.getSignature().getName() : countMetric.value();
        metricCollector.incrementCount("count." + metricName);
    }

    @Around("@annotation(timeMetric)")
    public Object timeMetrics(ProceedingJoinPoint joinPoint, TimeMetric timeMetric) throws Throwable {
        String metricName = timeMetric.value().isEmpty() ? joinPoint.getSignature().getName() : timeMetric.value();
        String timerName = "timer." + metricName;

        Instant start = Instant.now();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = Duration.between(start, Instant.now()).toMillis();
            metricCollector.recordDuration(timerName, duration);
        }
    }

    @AfterReturning(pointcut = "@annotation(successMetric)", returning = "result")
    public void afterReturningSuccessMetric(JoinPoint joinPoint, SuccessMetric successMetric, Object result) {
        String metricName = successMetric.value().isEmpty() ? joinPoint.getSignature().getName() : successMetric.value();
        String successCounterName = "success." + metricName;
        metricCollector.incrementSuccess(successCounterName);
    }

    @AfterThrowing(pointcut = "@annotation(errorMetric)", throwing = "ex")
    public void afterThrowingErrorMetric(JoinPoint joinPoint, ErrorMetric errorMetric, Throwable ex) {
        String metricName = errorMetric.value().isEmpty() ? joinPoint.getSignature().getName() : errorMetric.value();
        String errorCounterName = "error." + metricName;
        metricCollector.incrementError(errorCounterName);
    }
}
