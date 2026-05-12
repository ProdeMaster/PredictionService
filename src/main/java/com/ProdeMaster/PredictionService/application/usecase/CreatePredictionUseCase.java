package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.CreatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.EventPublisher;
import com.ProdeMaster.PredictionService.application.port.outbound.GroupServiceClient;
import com.ProdeMaster.PredictionService.application.port.outbound.MatchServiceClient;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.exception.InvalidPredictionException;
import com.ProdeMaster.PredictionService.domain.exception.PredictionAlreadyExistsException;
import com.ProdeMaster.PredictionService.domain.model.MatchScore;
import com.ProdeMaster.PredictionService.domain.model.Prediction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
    private final GroupServiceClient groupServiceClient;
    private final EventPublisher eventPublisher;
    private final int bufferMinutesBeforeMatch;

    public CreatePredictionUseCase(
            PredictionRepository predictionRepository,
            MatchServiceClient matchServiceClient,
            GroupServiceClient groupServiceClient,
            EventPublisher eventPublisher,
            int bufferMinutesBeforeMatch) {
        this.predictionRepository = predictionRepository;
        this.matchServiceClient = matchServiceClient;
        this.groupServiceClient = groupServiceClient;
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
     *   <li>Resolve target groups: if {@code groupId} is provided use it;
     *       otherwise fetch all groups the user belongs to via {@link GroupServiceClient}.</li>
     *   <li>For each target group: check for duplicate, build the aggregate, persist,
     *       and publish a {@code PredictionCreatedEvent}.</li>
     * </ol>
     */
    @Override
    public List<Prediction> create(String userId, String matchId, int homeTeamGoals, int awayTeamGoals, String groupId) {

        // 1. Fetch match — throws InvalidPredictionException if not found
        MatchServiceClient.MatchInfo matchInfo = fetchMatch(matchId);

        // 2. Domain rule: is the match accepting predictions?
        Prediction.validateMatchAcceptsPredictions(matchInfo.status());

        // 3. Cut-off time guard
        Instant matchStartTime = matchInfo.scheduledAt();
        Instant cutoffTime = matchStartTime.minus(bufferMinutesBeforeMatch, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(cutoffTime)) {
            throw new InvalidPredictionException(
                "Cannot create prediction. Match starts at " + matchStartTime +
                ". Predictions closed at " + cutoffTime);
        }

        // 4. Resolve target groups
        List<String> targetGroupIds = resolveTargetGroups(userId, groupId);

        // 5. Build score (shared across all groups)
        MatchScore predictedScore = MatchScore.of(homeTeamGoals, awayTeamGoals);

        // 6. Create one prediction per target group
        List<Prediction> results = new ArrayList<>();
        for (String targetGroupId : targetGroupIds) {

            // 6a. Duplicate check per (userId, matchId, groupId)
            if (predictionRepository.findByUserIdAndMatchIdAndGroupId(userId, matchId, targetGroupId).isPresent()) {
                throw new PredictionAlreadyExistsException(
                    "User " + userId + " already has a prediction for match " + matchId +
                    " in group " + targetGroupId);
            }

            // 6b. Create aggregate
            Prediction prediction = Prediction.create(userId, matchId, targetGroupId, predictedScore);

            // 6c. Persist
            Prediction saved = predictionRepository.save(prediction);
            results.add(saved);

            // 6d. Publish event
            eventPublisher.publishPredictionCreated(
                new EventPublisher.PredictionCreatedEvent(
                    saved.getId(),
                    saved.getUserId(),
                    saved.getMatchId(),
                    saved.getPredictedScore().getHomeTeamGoals(),
                    saved.getPredictedScore().getAwayTeamGoals(),
                    saved.getStatus().name(),
                    Instant.now()
                )
            );
        }

        return results;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<String> resolveTargetGroups(String userId, String groupId) {
        if (groupId != null && !groupId.isBlank()) {
            return List.of(groupId);
        }
        List<String> allGroups = groupServiceClient.getGroupIdsByUserId(userId);
        if (allGroups.isEmpty()) {
            throw new InvalidPredictionException(
                "No groupId provided and user " + userId + " does not belong to any group");
        }
        return allGroups;
    }

    private MatchServiceClient.MatchInfo fetchMatch(String matchId) {
        try {
            return matchServiceClient.getMatchById(matchId);
        } catch (Exception e) {
            throw new InvalidPredictionException("Match not found: " + matchId, e);
        }
    }
}
