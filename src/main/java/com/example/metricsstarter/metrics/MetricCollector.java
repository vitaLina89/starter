package com.example.metricsstarter.metrics;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
public class MetricCollector {

    private final ConcurrentHashMap<String, MethodMetrics> metricsMap = new ConcurrentHashMap<>();

    public void recordMethodCall(String metricName, long duration, boolean success) {
        metricsMap.computeIfAbsent(metricName, MethodMetrics::new).recordCall(duration, success);
    }

    public MethodMetrics getMetrics(String metricName) {
        return metricsMap.get(metricName);
    }

    public static class MethodMetrics {
        private final String name;
        private final LongAdder callCount = new LongAdder();
        private final LongAdder successCount = new LongAdder();
        private final LongAdder errorCount = new LongAdder();
        private final LongAdder totalDuration = new LongAdder();
        private final AtomicLong minDuration = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxDuration = new AtomicLong(Long.MIN_VALUE);

        public MethodMetrics(String name) {
            this.name = name;
        }

        public synchronized void recordCall(long duration, boolean success) {
            callCount.increment();
            totalDuration.add(duration);
            if (success) {
                successCount.increment();
            } else {
                errorCount.increment();
            }
            minDuration.accumulateAndGet(duration, Math::min);
            maxDuration.accumulateAndGet(duration, Math::max);
        }

        public long getCallCount() {
            return callCount.sum();
        }

        public long getSuccessCount() {
            return successCount.sum();
        }

        public long getErrorCount() {
            return errorCount.sum();
        }

        public long getTotalDuration() {
            return totalDuration.sum();
        }

        public long getMinDuration() {
            return minDuration.get();
        }

        public long getMaxDuration() {
            return maxDuration.get();
        }

        public double getAverageDuration() {
            return (double) totalDuration.sum() / callCount.sum();
        }
    }
}

