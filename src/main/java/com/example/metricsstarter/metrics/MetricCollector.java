package com.example.metricsstarter.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
public class MetricCollector {

    public static class DurationMetrics {
        private final AtomicLong minDuration = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxDuration = new AtomicLong(Long.MIN_VALUE);
        private final LongAdder totalDuration = new LongAdder();
        private final LongAdder count = new LongAdder();

        public void record(long duration) {
            totalDuration.add(duration);
            count.increment();

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
            return count.sum() == 0 ? 0 : totalDuration.sum() / count.sum();
        }
    }

    private final ConcurrentHashMap<String, LongAdder> countMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DurationMetrics> durationMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> successMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> errorMetrics = new ConcurrentHashMap<>();

    public void recordCount(String metricName) {
        countMetrics.computeIfAbsent(metricName, k -> new LongAdder()).increment();
    }

    public void recordDuration(String metricName, long duration) {
        durationMetrics.computeIfAbsent(metricName, k -> new DurationMetrics()).record(duration);
    }

    public void recordSuccessError(String metricName, boolean success) {
        if (success) {
            successMetrics.computeIfAbsent(metricName, k -> new LongAdder()).increment();
        } else {
            errorMetrics.computeIfAbsent(metricName, k -> new LongAdder()).increment();
        }
    }

    public Map<String, LongAdder> getCountMetrics() {
        return countMetrics;
    }

    public Map<String, DurationMetrics> getDurationMetrics() {
        return durationMetrics;
    }

    public Map<String, LongAdder> getSuccessMetrics() {
        return successMetrics;
    }

    public Map<String, LongAdder> getErrorMetrics() {
        return errorMetrics;
    }
}
