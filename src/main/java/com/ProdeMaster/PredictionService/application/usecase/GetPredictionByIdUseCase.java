package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionByIdInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.domain.exception.PredictionNotFoundException;
import com.ProdeMaster.PredictionService.domain.model.Prediction;

/**
 * Application use case: retrieve a single prediction by its unique ID.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No Spring annotations — pure Java, wired by an infrastructure {@code @Configuration}.</li>
 *   <li>Depends on {@link PredictionQueryPort} (outbound) for reads.</li>
 * </ul>
 */
public class GetPredictionByIdUseCase implements GetPredictionByIdInboundPort {

    private final PredictionQueryPort predictionQueryPort;

    public GetPredictionByIdUseCase(PredictionQueryPort predictionQueryPort) {
        this.predictionQueryPort = predictionQueryPort;
    }

    @Override
    public Prediction getById(String predictionId) {
        return predictionQueryPort.getById(predictionId)
            .orElseThrow(() -> new PredictionNotFoundException(
                "Prediction not found: " + predictionId));
    }
}