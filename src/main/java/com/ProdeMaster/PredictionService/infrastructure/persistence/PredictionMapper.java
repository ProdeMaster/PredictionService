package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.domain.model.MatchScore;
import com.ProdeMaster.PredictionService.domain.model.Prediction;

public class PredictionMapper {

    public static Prediction toDomain(PredictionJpaEntity entity) {
        if (entity == null) return null;

        MatchScore predictedScore = MatchScore.reconstitute(
                entity.getPredictedScore().getHomeTeamGoals(),
                entity.getPredictedScore().getAwayTeamGoals()
        );

        return Prediction.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getMatchId(),
                entity.getGroupId(),
                predictedScore,
                entity.getStatus(),
                entity.getMatchScheduledAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public static PredictionJpaEntity toEntity(Prediction domain) {
        if (domain == null) return null;

        MatchScoreEmbeddable predictedScore = new MatchScoreEmbeddable(
                domain.getPredictedScore().getHomeTeamGoals(),
                domain.getPredictedScore().getAwayTeamGoals()
        );

        return new PredictionJpaEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getMatchId(),
                domain.getGroupId(),
                predictedScore,
                domain.getStatus(),
                domain.getMatchScheduledAt(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );
    }
}
