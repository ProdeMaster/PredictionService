package com.ProdeMaster.PredictionService.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .permittedNumberOfCallsInHalfOpenState(5)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .failureRateThreshold(50)
                .build();

        return CircuitBreakerRegistry.of(config);
    }
}