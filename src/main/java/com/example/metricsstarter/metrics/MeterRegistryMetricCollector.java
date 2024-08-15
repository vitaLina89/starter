package com.example.metricsstarter.metrics;

import com.example.metricsstarter.interfaceabstract.MetricCollectorInterface;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Primary
@RequiredArgsConstructor
public class MeterRegistryMetricCollector implements MetricCollectorInterface {

    private final MeterRegistry meterRegistry;

    private final ConcurrentHashMap<String, AtomicLong> countMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DurationMetrics> durationMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> successMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> errorMetrics = new ConcurrentHashMap<>();

    @Override
    public void incrementCount(String metricName) {
        Counter counter = meterRegistry.find(metricName).counter();
        if (counter == null) {
            counter = meterRegistry.counter(metricName);
        }
        counter.increment();
        countMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    @Override
    public void recordDuration(String metricName, long duration) {
        Timer timer = meterRegistry.find(metricName).timer();
        if (timer == null) {
            timer = meterRegistry.timer(metricName);
        }
        timer.record(duration, TimeUnit.MILLISECONDS);
        durationMetrics.computeIfAbsent(metricName, k -> new DurationMetrics()).record(duration);
    }

    @Override
    public void incrementSuccess(String metricName) {
        Counter counter = meterRegistry.find(metricName).counter();
        if (counter == null) {
            counter = meterRegistry.counter(metricName);
        }
        counter.increment();
        successMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    @Override
    public void incrementError(String metricName) {
        Counter counter = meterRegistry.find(metricName).counter();
        if (counter == null) {
            counter = meterRegistry.counter(metricName);
        }
        counter.increment();
        errorMetrics.computeIfAbsent(metricName, k -> new AtomicLong()).incrementAndGet();
    }

    @Override
    public Map<String, AtomicLong> getCountMetrics() {
        return countMetrics;
    }

    @Override
    public Map<String, DurationMetrics> getDurationMetrics() {
        return durationMetrics;
    }

    @Override
    public Map<String, AtomicLong> getSuccessMetrics() {
        return successMetrics;
    }

    @Override
    public Map<String, AtomicLong> getErrorMetrics() {
        return errorMetrics;
    }

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

        public long getTotal() {
            return count.get() == 0 ? 0 : totalDuration.get() / count.get();
        }
    }
}
