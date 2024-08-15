package com.example.metricsstarter.interfaceabstract;

import com.example.metricsstarter.metrics.MetricCollector;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface MetricCollectorInterface {

    void incrementCount(String metricName);

    void recordDuration(String metricName, long duration);

    void incrementSuccess(String metricName);

    void incrementError(String metricName);

    Map<String, AtomicLong> getCountMetrics();

    Map<String, ?> getDurationMetrics();

    Map<String, AtomicLong> getSuccessMetrics();

    Map<String, AtomicLong> getErrorMetrics();
}
