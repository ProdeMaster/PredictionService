package com.ProdeMaster.PredictionService.domain.model;

/**
 * Represents the outcome of a football match derived from a {@link MatchScore}.
 *
 * <p>
 * This is a <strong>derived/computed</strong> domain concept — it is never
 * stored directly. It is always calculated from the exact scoreline via
 * {@link MatchScore#deriveOutcome()}.
 *
 * <p>
 * It exists to enable a scoring engine to award points based on different
 * levels of accuracy
 */
public enum MatchOutcome {
    HOME_WIN,
    DRAW,
    AWAY_WIN
}
