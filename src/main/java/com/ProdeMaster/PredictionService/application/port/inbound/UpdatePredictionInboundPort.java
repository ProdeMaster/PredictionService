package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

/**
 * Inbound port for the "update an existing prediction" use case.
 *
 * <p>
 * Only predictions in {@code PENDING} status can be updated.
 * The predicted score is expressed as exact goal counts.
 *
 * <p>
 * Implemented by: {@code UpdatePredictionUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface UpdatePredictionInboundPort {

    /**
     * Updates the predicted scoreline of an existing prediction.
     *
     * @param predictionId  ID of the prediction to update
     * @param homeTeamGoals new predicted goals for the home team (must be &ge; 0)
     * @param awayTeamGoals new predicted goals for the away team (must be &ge; 0)
     * @return the updated {@link Prediction}
     */
    Prediction update(String predictionId, int homeTeamGoals, int awayTeamGoals);
}
