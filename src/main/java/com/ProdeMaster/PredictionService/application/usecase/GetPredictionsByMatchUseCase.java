package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.domain.model.Prediction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Application use case: retrieve all predictions for a given match.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No Spring annotations — pure Java, wired by an infrastructure {@code @Configuration}.</li>
 *   <li>Depends on {@link PredictionQueryPort} (outbound) for reads.</li>
 * </ul>
 */
public class GetPredictionsByMatchUseCase implements GetPredictionsByMatchInboundPort {

    private final PredictionQueryPort predictionQueryPort;

    public GetPredictionsByMatchUseCase(PredictionQueryPort predictionQueryPort) {
        this.predictionQueryPort = predictionQueryPort;
    }

    @Override
    public Page<Prediction> getByMatchId(String matchId, Pageable pageable) {
        return predictionQueryPort.getByMatchId(matchId, pageable);
    }
}