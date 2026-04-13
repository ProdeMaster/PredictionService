package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

/**
 * Inbound port for fetching a single prediction by its ID.
 *
 * <p>
 * Implemented by: {@code GetPredictionByIdUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface GetPredictionByIdInboundPort {

    /**
     * Returns the prediction with the given ID.
     *
     * @param predictionId ID of the prediction to retrieve
     * @return the matching {@link Prediction}
     * @throws com.ProdeMaster.PredictionService.domain.exception.PredictionNotFoundException
     */
    Prediction getById(String predictionId);
}
