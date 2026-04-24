package com.ProdeMaster.PredictionService.infrastructure.messaging;

import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherImpl implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherImpl.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;

    public EventPublisherImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topic.prediction-events}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void publishPredictionCreated(PredictionCreatedEvent event) {
        log.info("Publishing prediction created event: {}", event.predictionId());
        kafkaTemplate.send(topicName, event.predictionId(), event);
    }

    @Override
    public void publishPredictionUpdated(PredictionUpdatedEvent event) {
        log.info("Publishing prediction updated event: {}", event.predictionId());
        kafkaTemplate.send(topicName, event.predictionId(), event);
    }

    @Override
    public void publishPredictionVoided(PredictionVoidedEvent event) {
        log.info("Publishing prediction voided event: {}", event.predictionId());
        kafkaTemplate.send(topicName, event.predictionId(), event);
    }
}