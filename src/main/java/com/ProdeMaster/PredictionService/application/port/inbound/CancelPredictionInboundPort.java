package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

/**
 * Inbound port for cancelling (voiding) a single prediction by the user.
 *
 * <p>
 * Implemented by: {@code CancelPredictionUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface CancelPredictionInboundPort {

    /**
     * Cancels a single prediction.
     *
     * @param predictionId ID of the prediction to cancel
     * @param reason       human-readable reason for the cancellation
     * @return the cancelled (voided) {@link Prediction}
     */
    Prediction cancel(String predictionId, String reason);
}
