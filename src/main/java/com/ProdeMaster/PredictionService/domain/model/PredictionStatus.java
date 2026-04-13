package com.ProdeMaster.PredictionService.domain.model;

/**
 * Represents the lifecycle state of a {@link Prediction}.
 *
 * <pre>
 *   PENDING ──lock()──────► LOCKED
 *   PENDING ──voidPrediction()──► VOIDED
 *   LOCKED  ──voidPrediction()──► VOIDED
 * </pre>
 *
 * <ul>
 * <li>{@link #PENDING} — The match has not started yet; the user may still
 * modify or cancel their predicted score.</li>
 * <li>{@link #LOCKED} — The match cut-off time has passed; no further changes
 * are allowed. The prediction awaits scoring once the match finishes.</li>
 * <li>{@link #VOIDED} — The prediction is invalid and will not be scored
 * (e.g. the match was cancelled or postponed indefinitely).</li>
 * </ul>
 */
public enum PredictionStatus {
    PENDING,
    LOCKED,
    VOIDED
}