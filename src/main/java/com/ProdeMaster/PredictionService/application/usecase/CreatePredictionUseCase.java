package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.CreatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import com.ProdeMaster.PredictionService.application.port.outbound.MatchServiceClient;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.exception.PredictionAlreadyExistsException;
import com.ProdeMaster.PredictionService.domain.model.MatchScore;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import com.ProdeMaster.PredictionService.domain.exception.InvalidPredictionException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Application use case: create a new prediction for a given user and match.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No Spring annotations — this class is pure Java and framework-agnostic.</li>
 *   <li>Instantiated and wired by an infrastructure {@code @Configuration} class
 *       that also reads {@code @Value("${app.prediction.buffer-minutes-before-match:5}")}
 *       and passes it to the constructor.</li>
 *   <li>Match-status validation logic is delegated to the domain via
 *       {@link Prediction#validateMatchAcceptsPredictions(String)}, keeping
 *       business rules inside the domain layer.</li>
 * </ul>
 */
public class CreatePredictionUseCase implements CreatePredictionInboundPort {

    private final PredictionRepository predictionRepository;
    private final MatchServiceClient matchServiceClient;
    private final EventPublisher eventPublisher;
    private final int bufferMinutesBeforeMatch;

    public CreatePredictionUseCase(
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
     *   <li>Fetch match data and validate it exists.</li>
     *   <li>Delegate match-status check to the domain rule
     *       {@link Prediction#validateMatchAcceptsPredictions(String)}.</li>
     *   <li>Validate the cut-off time has not yet passed.</li>
     *   <li>Validate no duplicate prediction exists for this user + match.</li>
     *   <li>Build a {@link MatchScore} and create the {@link Prediction} aggregate.</li>
     *   <li>Persist and publish a {@code PredictionCreatedEvent}.</li>
     * </ol>
     */
    @Override
    public Prediction create(String userId, String matchId, int homeTeamGoals, int awayTeamGoals) {

        // 1. Fetch match — throws InvalidPredictionException if not found
        MatchServiceClient.MatchInfo matchInfo = fetchMatch(matchId);

        // 2. Domain rule: is the match accepting predictions? (delegated to domain)
        Prediction.validateMatchAcceptsPredictions(matchInfo.status());

        // 3. Cut-off time guard
        Instant matchStartTime = matchInfo.scheduledAt();
        Instant cutoffTime = matchStartTime.minus(bufferMinutesBeforeMatch, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(cutoffTime)) {
            throw new InvalidPredictionException(
                "Cannot create prediction. Match starts at " + matchStartTime +
                ". Predictions closed at " + cutoffTime);
        }

        // 4. Duplicate check
        if (predictionRepository.findByUserIdAndMatchId(userId, matchId).isPresent()) {
            throw new PredictionAlreadyExistsException(
                "User " + userId + " already has a prediction for match " + matchId);
        }

        // 5. Build domain value object + aggregate
        MatchScore predictedScore = MatchScore.of(homeTeamGoals, awayTeamGoals);
        Prediction prediction = Prediction.create(userId, matchId, predictedScore);

        // 6. Persist
        Prediction savedPrediction = predictionRepository.save(prediction);

        // 7. Publish event
        eventPublisher.publishPredictionCreated(
            new EventPublisher.PredictionCreatedEvent(
                savedPrediction.getId(),
                savedPrediction.getUserId(),
                savedPrediction.getMatchId(),
                savedPrediction.getPredictedScore().getHomeTeamGoals(),
                savedPrediction.getPredictedScore().getAwayTeamGoals(),
                savedPrediction.getStatus().name(),
                Instant.now()
            )
        );

        return savedPrediction;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private MatchServiceClient.MatchInfo fetchMatch(String matchId) {
        try {
            return matchServiceClient.getMatch(matchId);
        } catch (Exception e) {
            throw new InvalidPredictionException("Match not found: " + matchId, e);
        }
    }
}