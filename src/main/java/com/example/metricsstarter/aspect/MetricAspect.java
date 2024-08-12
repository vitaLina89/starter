package com.example.metricsstarter.aspect;

import com.example.metricsstarter.annotation.CountMetric;
import com.example.metricsstarter.annotation.ErrorMetric;
import com.example.metricsstarter.annotation.SuccessMetric;
import com.example.metricsstarter.annotation.TimeMetric;
import com.example.metricsstarter.metrics.MetricCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final MetricCollector metricCollector;
    private final MeterRegistry meterRegistry;
    @Value("${metrics.useActuator:false}")
    private boolean useActuator;

    @Before("@annotation(countMetric)")
    public void countMetrics(JoinPoint joinPoint, CountMetric countMetric) {
        String metricName = countMetric.value().isEmpty() ? joinPoint.getSignature().getName() : countMetric.value();
        String counterName = "count." + metricName;

        if (useActuator) {
            Counter counter = meterRegistry.find(counterName).counter();
            if (counter == null) {
                counter = meterRegistry.counter(counterName);
            }
            counter.increment();
        } else {
            metricCollector.recordCount(counterName);
        }
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
            if (useActuator) {
                Timer timer = meterRegistry.find(timerName).timer();
                if (timer == null) {
                    timer = meterRegistry.timer(timerName);
                }
                timer.record(duration, TimeUnit.MILLISECONDS);
            } else {
                metricCollector.recordDuration(timerName, duration);
            }
        }
    }

    @AfterReturning(pointcut = "@annotation(successMetric)", returning = "result")
    public void afterReturningSuccessMetric(JoinPoint joinPoint, SuccessMetric successMetric, Object result) {
        String metricName = successMetric.value().isEmpty() ? joinPoint.getSignature().getName() : successMetric.value();
        String successCounterName = "success." + metricName;

        if (useActuator) {
            Counter counter = meterRegistry.find(successCounterName).counter();
            if (counter == null) {
                counter = meterRegistry.counter(successCounterName);
            }
            counter.increment();
        } else {
            metricCollector.recordSuccess(successCounterName);
        }
    }

    @AfterThrowing(pointcut = "@annotation(errorMetric)", throwing = "ex")
    public void afterThrowingErrorMetric(JoinPoint joinPoint, ErrorMetric errorMetric, Throwable ex) {
        String metricName = errorMetric.value().isEmpty() ? joinPoint.getSignature().getName() : errorMetric.value();
        String errorCounterName = "error." + metricName;

        if (useActuator) {
            Counter counter = meterRegistry.find(errorCounterName).counter();
            if (counter == null) {
                counter = meterRegistry.counter(errorCounterName);
            }
            counter.increment();
        } else {
            metricCollector.recordError(errorCounterName);
        }
    }

}
