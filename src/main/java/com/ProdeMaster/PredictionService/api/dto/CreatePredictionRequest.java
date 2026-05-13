package com.ProdeMaster.PredictionService.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePredictionRequest(
        @NotBlank(message = "Match ID is required") String matchId,

        @NotNull(message = "Home team goals is required") @Min(value = 0, message = "Home team goals cannot be negative") Integer homeTeamGoals,

        @NotNull(message = "Away team goals is required") @Min(value = 0, message = "Away team goals cannot be negative") Integer awayTeamGoals,

        // Nullable: if null, the prediction is created for all groups the user belongs
        // to.
        String groupId) {
}