package com.example.metricsstarter.config;

import lombok.Getter;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Getter
@AutoConfiguration
@ComponentScan("com.example.metricsstarter")
@EnableAspectJAutoProxy
@ConfigurationProperties(prefix = "metrics")
public class MetricConfig {

    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

