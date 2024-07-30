package com.example.metricsstarter.controller;

import com.example.metricsstarter.metrics.MetricCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {


    private final MetricCollector metricCollector;

    @GetMapping("/{metricName}")
    public Map<String, Object> getMetrics(@PathVariable String metricName) {
        MetricCollector.MethodMetrics metrics = metricCollector.getMetrics(metricName);
        if (metrics == null) {
            return Map.of("error", "Metric not found");
        }
        return Map.of(
                "callCount", metrics.getCallCount(),
                "successCount", metrics.getSuccessCount(),
                "errorCount", metrics.getErrorCount(),
                "totalDuration", metrics.getTotalDuration(),
                "averageDuration", metrics.getAverageDuration(),
                "minDuration", metrics.getMinDuration(),
                "maxDuration", metrics.getMaxDuration()
        );
    }
}

