package com.ProdeMaster.PredictionService.application.port.inbound;

/**
 * Inbound port for locking all pending predictions associated with a match.
 *
 * <p>
 * Typically triggered by a domain event when the match cut-off time arrives
 * (e.g. a few minutes before kick-off). Once locked, predictions cannot be
 * modified by the user.
 *
 * <p>
 * Implemented by: {@code CancelPredictionUseCase}.
 * Consumed by: event-listener adapters in the infrastructure layer.
 */
public interface LockPredictionsForMatchInboundPort {

    /**
     * Locks all {@code PENDING} predictions for the given match.
     *
     * @param matchId ID of the match whose predictions should be locked
     */
    void lockPredictionsForMatch(String matchId);
}
