package com.ProdeMaster.PredictionService.application.port.outbound;

import java.time.Instant;

/**
 * Outbound port for publishing domain events to an asynchronous message broker
 * (e.g. Kafka, RabbitMQ).
 *
 * <p>Each method accepts an inner record representing a fully-formed event payload.
 * Events carry exact scoreline data ({@code homeTeamGoals}/{@code awayTeamGoals})
 * rather than the deprecated {@code PredictionResult} enum, reflecting the
 * correct domain model of the application.
 */
public interface EventPublisher {

    void publishPredictionCreated(PredictionCreatedEvent event);

    void publishPredictionUpdated(PredictionUpdatedEvent event);

    void publishPredictionVoided(PredictionVoidedEvent event);

    /**
     * Event published when a new prediction is successfully persisted.
     *
     * @param predictionId  ID of the new prediction
     * @param userId        ID of the user who made the prediction
     * @param matchId       ID of the match being predicted
     * @param homeTeamGoals predicted goals for the home team
     * @param awayTeamGoals predicted goals for the away team
     * @param status        initial lifecycle status (always "PENDING")
     * @param timestamp     moment the event was generated
     */
    record PredictionCreatedEvent(
        String predictionId,
        String userId,
        String matchId,
        int homeTeamGoals,
        int awayTeamGoals,
        String status,
        Instant timestamp
    ) {}

    /**
     * Event published when an existing prediction's scoreline is changed.
     *
     * @param predictionId     ID of the updated prediction
     * @param userId           ID of the user who owns the prediction
     * @param matchId          ID of the match
     * @param oldHomeTeamGoals previous predicted goals for the home team
     * @param oldAwayTeamGoals previous predicted goals for the away team
     * @param newHomeTeamGoals new predicted goals for the home team
     * @param newAwayTeamGoals new predicted goals for the away team
     * @param timestamp        moment the event was generated
     */
    record PredictionUpdatedEvent(
        String predictionId,
        String userId,
        String matchId,
        int oldHomeTeamGoals,
        int oldAwayTeamGoals,
        int newHomeTeamGoals,
        int newAwayTeamGoals,
        Instant timestamp
    ) {}

    /**
     * Event published when a prediction is voided (cancelled by user or by match cancellation).
     *
     * @param predictionId ID of the voided prediction
     * @param userId       ID of the user who owns the prediction
     * @param matchId      ID of the match
     * @param reason       human-readable reason for voiding
     * @param timestamp    moment the event was generated
     */
    record PredictionVoidedEvent(
        String predictionId,
        String userId,
        String matchId,
        String reason,
        Instant timestamp
    ) {}
}