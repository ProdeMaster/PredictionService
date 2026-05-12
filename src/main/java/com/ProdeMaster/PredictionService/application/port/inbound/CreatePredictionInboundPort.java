package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import java.util.List;

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
     * Creates one or more predictions for the given user and match.
     *
     * <p>When {@code groupId} is provided, a single prediction is created for that
     * group. When {@code groupId} is {@code null}, one prediction is created for
     * every group the user currently belongs to (resolved via {@code GroupServiceClient}).
     *
     * @param userId        ID of the user making the prediction
     * @param matchId       ID of the match being predicted
     * @param homeTeamGoals number of goals the user predicts for the home team (must be &ge; 0)
     * @param awayTeamGoals number of goals the user predicts for the away team (must be &ge; 0)
     * @param groupId       ID of the target group, or {@code null} to target all groups
     * @return list of persisted {@link Prediction} instances (one per target group)
     */
    List<Prediction> create(String userId, String matchId, int homeTeamGoals, int awayTeamGoals, String groupId);
}
