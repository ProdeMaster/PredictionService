package com.ProdeMaster.PredictionService.api.dto;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import java.time.Instant;

public record PredictionResponse(
    String id,
    String userId,
    String matchId,
    String groupId,
    int homeTeamGoals,
    int awayTeamGoals,
    String outcome,
    String status,
    Instant createdAt,
    Instant updatedAt
) {
    public static PredictionResponse fromDomain(Prediction prediction) {
        return new PredictionResponse(
            prediction.getId(),
            prediction.getUserId(),
            prediction.getMatchId(),
            prediction.getGroupId(),
            prediction.getPredictedScore().getHomeTeamGoals(),
            prediction.getPredictedScore().getAwayTeamGoals(),
            prediction.getDerivedOutcome().name(),
            prediction.getStatus().name(),
            prediction.getCreatedAt(),
            prediction.getUpdatedAt()
        );
    }
}