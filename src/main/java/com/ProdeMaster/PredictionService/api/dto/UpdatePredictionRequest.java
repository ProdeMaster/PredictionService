package com.ProdeMaster.PredictionService.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdatePredictionRequest(
    @NotNull(message = "Home team goals is required")
    @Min(value = 0, message = "Home team goals cannot be negative")
    Integer homeTeamGoals,

    @NotNull(message = "Away team goals is required")
    @Min(value = 0, message = "Away team goals cannot be negative")
    Integer awayTeamGoals
) {}