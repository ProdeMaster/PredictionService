package com.ProdeMaster.PredictionService.infrastructure.config;

import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import com.ProdeMaster.PredictionService.application.port.outbound.MatchServiceClient;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.application.usecase.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infrastructure configuration class mapping domain/application uses cases
 * to Spring Beans. This keeps the application layer clean of Spring annotations.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePredictionUseCase createPredictionUseCase(
            PredictionRepository predictionRepository,
            MatchServiceClient matchServiceClient,
            EventPublisher eventPublisher,
            @Value("${app.prediction.buffer-minutes-before-match:5}") int bufferMinutesBeforeMatch) {
        return new CreatePredictionUseCase(
                predictionRepository, matchServiceClient, eventPublisher, bufferMinutesBeforeMatch);
    }

    @Bean
    public UpdatePredictionUseCase updatePredictionUseCase(
            PredictionRepository predictionRepository,
            MatchServiceClient matchServiceClient,
            EventPublisher eventPublisher,
            @Value("${app.prediction.buffer-minutes-before-match:5}") int bufferMinutesBeforeMatch) {
        return new UpdatePredictionUseCase(
                predictionRepository, matchServiceClient, eventPublisher, bufferMinutesBeforeMatch);
    }

    @Bean
    public CancelPredictionUseCase cancelPredictionUseCase(
            PredictionRepository predictionRepository,
            EventPublisher eventPublisher) {
        return new CancelPredictionUseCase(predictionRepository, eventPublisher);
    }

    @Bean
    public GetPredictionByIdUseCase getPredictionByIdUseCase(
            PredictionQueryPort predictionQueryPort) {
        return new GetPredictionByIdUseCase(predictionQueryPort);
    }

    @Bean
    public GetPredictionsByMatchUseCase getPredictionsByMatchUseCase(
            PredictionQueryPort predictionQueryPort) {
        return new GetPredictionsByMatchUseCase(predictionQueryPort);
    }

    @Bean
    public GetPredictionsByUserUseCase getPredictionsByUserUseCase(
            PredictionQueryPort predictionQueryPort) {
        return new GetPredictionsByUserUseCase(predictionQueryPort);
    }
}
