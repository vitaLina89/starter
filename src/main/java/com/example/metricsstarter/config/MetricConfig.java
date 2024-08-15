package com.example.metricsstarter.config;

import com.example.metricsstarter.aspect.MetricAspect;
import com.example.metricsstarter.interfaceabstract.MetricCollectorInterface;
import com.example.metricsstarter.metrics.MeterRegistryMetricCollector;
import com.example.metricsstarter.metrics.MetricCollector;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Getter
@AutoConfiguration
@ComponentScan("com.example.metricsstarter")
@EnableAspectJAutoProxy
@Slf4j
@ConfigurationProperties(prefix = "metrics")
public class MetricConfig {

    @Value("${metrics.useActuator:true}")
    private boolean useActuator;

    @Bean
    @ConditionalOnProperty(prefix = "metrics", name = "useActuator", havingValue = "true", matchIfMissing = true)
    public MeterRegistryMetricCollector metricCollector( MeterRegistry meterRegistry) {
        log.info("Micrometer MeterRegistry bean created.");
        return new MeterRegistryMetricCollector(meterRegistry);
    }

    @Bean
    @ConditionalOnProperty(prefix = "metrics", name = "useActuator", havingValue = "false")
    public MetricCollector customMetricCollector() {
        log.info("Custom MetricCollector bean created.");
        return new MetricCollector();
    }

    @Bean
    public MetricAspect metricAspect(MetricCollectorInterface metricCollector) {
        return new MetricAspect(metricCollector);
    }
}
