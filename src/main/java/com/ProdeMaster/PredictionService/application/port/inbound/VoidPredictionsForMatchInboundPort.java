package com.ProdeMaster.PredictionService.application.port.inbound;

/**
 * Inbound port for voiding all predictions associated with a match.
 *
 * <p>
 * Typically triggered when a match is cancelled or postponed indefinitely.
 * Voided predictions are excluded from scoring calculations.
 *
 * <p>
 * Implemented by: {@code CancelPredictionUseCase}.
 * Consumed by: event-listener adapters in the infrastructure layer.
 */
public interface VoidPredictionsForMatchInboundPort {

    /**
     * Voids all non-voided predictions for the given match.
     *
     * @param matchId ID of the match whose predictions should be voided
     * @param reason  human-readable reason (e.g. "Match cancelled")
     */
    void voidPredictionsForMatch(String matchId, String reason);
}
