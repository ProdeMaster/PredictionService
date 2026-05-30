package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable pagination parameters
     * @return paginated list of {@link Prediction} objects
     */
    Page<Prediction> getByMatchId(String matchId, Pageable pageable);
}
