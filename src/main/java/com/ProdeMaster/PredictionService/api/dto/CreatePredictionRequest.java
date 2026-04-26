package com.ProdeMaster.PredictionService.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePredictionRequest(
    @NotBlank(message = "User ID is required")
    String userId,

    @NotBlank(message = "Match ID is required")
    String matchId,

    @NotNull(message = "Home team goals is required")
    @Min(value = 0, message = "Home team goals cannot be negative")
    Integer homeTeamGoals,

    @NotNull(message = "Away team goals is required")
    @Min(value = 0, message = "Away team goals cannot be negative")
    Integer awayTeamGoals
) {}