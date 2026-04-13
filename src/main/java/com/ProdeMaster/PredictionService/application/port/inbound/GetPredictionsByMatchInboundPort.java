package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

import java.util.List;

/**
 * Inbound port for fetching all predictions associated with a given match.
 *
 * <p>
 * Implemented by: {@code GetPredictionsByMatchUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface GetPredictionsByMatchInboundPort {

    /**
     * Returns all predictions for the given match.
     *
     * @param matchId ID of the match
     * @return list of {@link Prediction} objects (may be empty, never null)
     */
    List<Prediction> getByMatchId(String matchId);
}
