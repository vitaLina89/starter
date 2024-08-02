package com.example.metricsstarter.controller;

import com.example.metricsstarter.metrics.MetricCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricCollector metricCollector;

    @GetMapping("/count/{metricName}")
    public Map<String, Object> getCountMetrics(@PathVariable String metricName) {
        LongAdder count = metricCollector.getCountMetrics().get(metricName);
        return Map.of("callCount", count == null ? 0 : count.sum());
    }

    @GetMapping("/duration/{metricName}")
    public Map<String, Object> getDurationMetrics(@PathVariable String metricName) {
        MetricCollector.DurationMetrics metrics = metricCollector.getDurationMetrics().get(metricName);
        return metrics == null
                ? Map.of("minDuration", 0, "maxDuration", 0, "avgDuration", 0)
                : Map.of(
                "minDuration", metrics.getMin(),
                "maxDuration", metrics.getMax(),
                "avgDuration", metrics.getAvg()
        );
    }

    @GetMapping("/success-error/{metricName}")
    public Map<String, Object> getSuccessErrorMetrics(@PathVariable String metricName) {
        LongAdder success = metricCollector.getSuccessMetrics().get(metricName);
        LongAdder error = metricCollector.getErrorMetrics().get(metricName);
        return Map.of(
                "successCount", success == null ? 0 : success.sum(),
                "errorCount", error == null ? 0 : error.sum()
        );
    }
}
