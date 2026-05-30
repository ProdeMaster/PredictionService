package com.ProdeMaster.PredictionService.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.lang.NonNull;

@Configuration
public class KafkaConfig {

    private final String predictionEventsTopic;

    public KafkaConfig(@NonNull @Value("${app.kafka.topic.prediction-events}") String predictionEventsTopic) {
        this.predictionEventsTopic = predictionEventsTopic;
    }

    @Bean
    public NewTopic predictionEventsTopic() {
        return TopicBuilder.name(predictionEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}