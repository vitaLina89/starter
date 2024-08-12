package com.example.metricsstarter.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MetricCollector {

    public static class DurationMetrics {
        private final AtomicLong minDuration = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxDuration = new AtomicLong(Long.MIN_VALUE);
        private final AtomicLong totalDuration = new AtomicLong();
        private final AtomicLong count = new AtomicLong();

        public void record(long duration) {
            totalDuration.addAndGet(duration);
            count.incrementAndGet();

            minDuration.getAndUpdate(min -> Math.min(min, duration));
            maxDuration.getAndUpdate(max -> Math.max(max, duration));
        }

        public long getMin() {
            return minDuration.get();
        }

        public long getMax() {
            return maxDuration.get();
        }

        public long getAvg() {
            return count.get() == 0 ? 0 : totalDuration.get() / count.get();
        }
    }

    private final ConcurrentHashMap<String, AtomicLong> countMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DurationMetrics> durationMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> successMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> errorMetrics = new ConcurrentHashMap<>();

    public void recordCount(String metricName) {
        countMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    public void recordDuration(String metricName, long duration) {
        durationMetrics.computeIfAbsent(metricName, k -> new DurationMetrics()).record(duration);
    }

    public void recordSuccess(String metricName) {
        successMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    public void recordError(String metricName) {
        errorMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    public Map<String, AtomicLong> getCountMetrics() {
        return countMetrics;
    }

    public Map<String, DurationMetrics> getDurationMetrics() {
        return durationMetrics;
    }

    public Map<String, AtomicLong> getSuccessMetrics() {
        return successMetrics;
    }

    public Map<String, AtomicLong> getErrorMetrics() {
        return errorMetrics;
    }
}

