package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;

/**
 * Inbound port for the "create a new prediction" use case.
 *
 * <p>
 * The predicted score is expressed as two separate integers to reflect the
 * exact scoreline model ({@code homeTeamGoals} and {@code awayTeamGoals}).
 * The application layer is responsible for constructing the
 * {@link com.ProdeMaster.PredictionService.domain.model.MatchScore}
 * value object from these parameters before delegating to the domain.
 *
 * <p>
 * Implemented by: {@code CreatePredictionUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface CreatePredictionInboundPort {

    /**
     * Creates a new prediction for the given user and match.
     *
     * @param userId        ID of the user making the prediction
     * @param matchId       ID of the match being predicted
     * @param homeTeamGoals number of goals the user predicts for the home team
     *                      (must be &ge; 0)
     * @param awayTeamGoals number of goals the user predicts for the away team
     *                      (must be &ge; 0)
     * @return the persisted {@link Prediction}
     */
    Prediction create(String userId, String matchId, int homeTeamGoals, int awayTeamGoals);
}
