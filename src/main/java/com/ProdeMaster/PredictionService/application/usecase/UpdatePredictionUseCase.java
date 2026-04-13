package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.UpdatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import com.ProdeMaster.PredictionService.application.port.outbound.MatchServiceClient;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.exception.InvalidPredictionException;
import com.ProdeMaster.PredictionService.domain.exception.PredictionNotFoundException;
import com.ProdeMaster.PredictionService.domain.model.MatchScore;
import com.ProdeMaster.PredictionService.domain.model.Prediction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Application use case: update the predicted scoreline of an existing prediction.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No Spring annotations — pure Java, framework-agnostic.</li>
 *   <li>{@code bufferMinutesBeforeMatch} is injected via constructor by an
 *       infrastructure {@code @Configuration} class that reads it from properties.</li>
 * </ul>
 */
public class UpdatePredictionUseCase implements UpdatePredictionInboundPort {

    private final PredictionRepository predictionRepository;
    private final MatchServiceClient matchServiceClient;
    private final EventPublisher eventPublisher;
    private final int bufferMinutesBeforeMatch;

    public UpdatePredictionUseCase(
            PredictionRepository predictionRepository,
            MatchServiceClient matchServiceClient,
            EventPublisher eventPublisher,
            int bufferMinutesBeforeMatch) {
        this.predictionRepository = predictionRepository;
        this.matchServiceClient = matchServiceClient;
        this.eventPublisher = eventPublisher;
        this.bufferMinutesBeforeMatch = bufferMinutesBeforeMatch;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Execution flow:
     * <ol>
     *   <li>Load the prediction — throws {@link PredictionNotFoundException} if absent.</li>
     *   <li>Validate the prediction is still modifiable (PENDING status).</li>
     *   <li>Validate the match cut-off time has not yet passed.</li>
     *   <li>Build a new {@link MatchScore} and delegate the update to the domain aggregate.</li>
     *   <li>Persist and publish a {@code PredictionUpdatedEvent} with old and new scores.</li>
     * </ol>
     */
    @Override
    public Prediction update(String predictionId, int homeTeamGoals, int awayTeamGoals) {

        // 1. Load prediction
        Prediction prediction = predictionRepository.findById(predictionId)
            .orElseThrow(() -> new PredictionNotFoundException(
                "Prediction not found: " + predictionId));

        // 2. Domain guard — canModify() checks PENDING status
        if (!prediction.canModify()) {
            throw new InvalidPredictionException(
                "Cannot update prediction. Current status: " + prediction.getStatus());
        }

        // 3. Capture old score before mutation (for the event payload)
        MatchScore oldScore = prediction.getPredictedScore();

        // 4. Cut-off time guard
        MatchServiceClient.MatchInfo matchInfo = matchServiceClient.getMatch(prediction.getMatchId());
        Instant matchStartTime = matchInfo.scheduledAt();
        Instant cutoffTime = matchStartTime.minus(bufferMinutesBeforeMatch, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(cutoffTime)) {
            throw new InvalidPredictionException(
                "Cannot update prediction. Match starts at " + matchStartTime +
                ". Predictions closed at " + cutoffTime);
        }

        // 5. Build new score + delegate update to domain aggregate
        MatchScore newScore = MatchScore.of(homeTeamGoals, awayTeamGoals);
        prediction.updateScore(newScore);

        // 6. Persist
        Prediction updatedPrediction = predictionRepository.save(prediction);

        // 7. Publish event with both old and new exact scores
        eventPublisher.publishPredictionUpdated(
            new EventPublisher.PredictionUpdatedEvent(
                updatedPrediction.getId(),
                updatedPrediction.getUserId(),
                updatedPrediction.getMatchId(),
                oldScore.getHomeTeamGoals(),
                oldScore.getAwayTeamGoals(),
                newScore.getHomeTeamGoals(),
                newScore.getAwayTeamGoals(),
                Instant.now()
            )
        );

        return updatedPrediction;
    }
}