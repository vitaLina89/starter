package com.example.metricsstarter.controller;

import com.example.metricsstarter.interfaceabstract.MetricCollectorInterface;
import com.example.metricsstarter.metrics.MetricCollector;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricCollectorInterface metricCollector;

    @GetMapping("/all")
    public Map<String, Object> getAllMetrics() {

            return Map.of(
                    "countMetrics", metricCollector.getCountMetrics(),
                    "durationMetrics", metricCollector.getDurationMetrics(),
                    "successMetrics", metricCollector.getSuccessMetrics(),
                    "errorMetrics", metricCollector.getErrorMetrics()
            );

    }


}


