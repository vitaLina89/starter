package com.example.metricsstarter.controller;

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

    private final MetricCollector metricCollector;
    private final MeterRegistry meterRegistry;
    @Value("${metrics.useActuator:false}")
    private boolean useActuator;

    @GetMapping("/all")
    public Map<String, Object> getAllMetrics() {
        if (useActuator) {
            Map<String, Object> countMetrics = meterRegistry.getMeters().stream()
                    .filter(meter -> meter.getId().getType() == Meter.Type.COUNTER)
                    .collect(Collectors.toMap(
                            meter -> "count." + meter.getId().getName(),
                            meter -> meter.measure(),
                            (existing, replacement) -> existing
                    ));

            Map<String, Object> durationMetrics = meterRegistry.getMeters().stream()
                    .filter(meter -> meter.getId().getType() == Meter.Type.TIMER)
                    .collect(Collectors.toMap(
                            meter -> "timer." + meter.getId().getName(),
                            meter -> meter.measure(),
                            (existing, replacement) -> existing
                    ));

            Map<String, Object> successMetrics = meterRegistry.getMeters().stream()
                    .filter(meter -> meter.getId().getName().startsWith("success."))
                    .collect(Collectors.toMap(
                            meter -> meter.getId().getName(),
                            meter -> meter.measure(),
                            (existing, replacement) -> existing
                    ));

            Map<String, Object> errorMetrics = meterRegistry.getMeters().stream()
                    .filter(meter -> meter.getId().getName().startsWith("error."))
                    .collect(Collectors.toMap(
                            meter -> meter.getId().getName(),
                            meter -> meter.measure(),
                            (existing, replacement) -> existing
                    ));

            return Map.of(
                    "countMetrics", countMetrics,
                    "durationMetrics", durationMetrics,
                    "successMetrics", successMetrics,
                    "errorMetrics", errorMetrics
            );
        } else {
            return Map.of(
                    "countMetrics", metricCollector.getCountMetrics(),
                    "durationMetrics", metricCollector.getDurationMetrics(),
                    "successMetrics", metricCollector.getSuccessMetrics(),
                    "errorMetrics", metricCollector.getErrorMetrics()
            );
        }
    }


}


