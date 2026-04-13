package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.CancelPredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.LockPredictionsForMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.VoidPredictionsForMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.exception.PredictionNotFoundException;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import com.ProdeMaster.PredictionService.domain.model.PredictionStatus;

import java.time.Instant;
import java.util.List;

/**
 * Application use case: handles prediction lifecycle termination events.
 *
 * <p>Implements three focused inbound ports related to the end-of-life of predictions:
 * <ul>
 *   <li>{@link CancelPredictionInboundPort}         — user cancels their own prediction</li>
 *   <li>{@link LockPredictionsForMatchInboundPort}  — system locks all predictions when match starts</li>
 *   <li>{@link VoidPredictionsForMatchInboundPort}  — system voids all predictions when match is cancelled</li>
 * </ul>
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No Spring annotations — pure Java, wired by an infrastructure {@code @Configuration}.</li>
 *   <li>Lock and void operations are triggered by message-broker events in the infra layer,
 *       not by direct HTTP calls.</li>
 * </ul>
 */
public class CancelPredictionUseCase
        implements CancelPredictionInboundPort,
                   LockPredictionsForMatchInboundPort,
                   VoidPredictionsForMatchInboundPort {

    private final PredictionRepository predictionRepository;
    private final EventPublisher eventPublisher;

    public CancelPredictionUseCase(
            PredictionRepository predictionRepository,
            EventPublisher eventPublisher) {
        this.predictionRepository = predictionRepository;
        this.eventPublisher = eventPublisher;
    }

    // -------------------------------------------------------------------------
    // CancelPredictionInboundPort
    // -------------------------------------------------------------------------

    /**
     * Voids a single prediction upon user request.
     * Idempotent — if already voided the domain no-ops; the event is still emitted.
     */
    @Override
    public Prediction cancel(String predictionId, String reason) {
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new PredictionNotFoundException(
                "Prediction not found: " + predictionId));

        prediction.voidPrediction(reason);
        Prediction cancelledPrediction = predictionRepository.save(prediction);

        eventPublisher.publishPredictionVoided(
            new EventPublisher.PredictionVoidedEvent(
                cancelledPrediction.getId(),
                cancelledPrediction.getUserId(),
                cancelledPrediction.getMatchId(),
                reason,
                Instant.now()
            )
        );

        return cancelledPrediction;
    }

    // -------------------------------------------------------------------------
    // LockPredictionsForMatchInboundPort
    // -------------------------------------------------------------------------

    /**
     * Locks all {@code PENDING} predictions for the given match.
     * Triggered when the match cut-off time is reached (e.g. via a scheduled event).
     * No event is published per locked prediction — locking is an internal state change.
     */
    @Override
    public void lockPredictionsForMatch(String matchId) {
        List<Prediction> predictions = predictionRepository.findByMatchIdAndStatus(
            matchId,
            PredictionStatus.PENDING.name()
        );

        for (Prediction prediction : predictions) {
            prediction.lock();
            predictionRepository.save(prediction);
        }
    }

    // -------------------------------------------------------------------------
    // VoidPredictionsForMatchInboundPort
    // -------------------------------------------------------------------------

    /**
     * Voids all non-voided predictions for a match that has been cancelled or postponed.
     * Publishes a {@code PredictionVoidedEvent} for each voided prediction.
     */
    @Override
    public void voidPredictionsForMatch(String matchId, String reason) {
        List<Prediction> predictions = predictionRepository.findByMatchId(matchId);

        for (Prediction prediction : predictions) {
            if (prediction.getStatus() != PredictionStatus.VOIDED) {
                prediction.voidPrediction(reason);
                predictionRepository.save(prediction);

                eventPublisher.publishPredictionVoided(
                    new EventPublisher.PredictionVoidedEvent(
                        prediction.getId(),
                        prediction.getUserId(),
                        prediction.getMatchId(),
                        reason,
                        Instant.now()
                    )
                );
            }
        }
    }
}