package com.ProdeMaster.PredictionService.application.port.inbound;

/**
 * @deprecated This "god interface" violated the Interface Segregation Principle (ISP).
 * It has been replaced by the following focused inbound ports, one per use case:
 *
 * <ul>
 *   <li>{@link CreatePredictionInboundPort} — create a new prediction</li>
 *   <li>{@link UpdatePredictionInboundPort} — update an existing prediction's score</li>
 *   <li>{@link CancelPredictionInboundPort} — cancel a single prediction</li>
 *   <li>{@link LockPredictionsForMatchInboundPort} — lock all predictions for a match</li>
 *   <li>{@link VoidPredictionsForMatchInboundPort} — void all predictions for a match</li>
 * </ul>
 *
 * <p>Additionally, the old method signatures accepted {@code PredictionResult} (HOME/DRAW/AWAY),
 * which has been superseded by exact goal counts ({@code int homeTeamGoals, int awayTeamGoals}).
 *
 * <p><strong>This file must be deleted once the infrastructure/API layers
 * no longer reference it.</strong>
 */
@Deprecated(since = "application-refactor", forRemoval = true)
public interface PredictionCommandPort {
    // Intentionally empty — see Javadoc above for replacements.
}