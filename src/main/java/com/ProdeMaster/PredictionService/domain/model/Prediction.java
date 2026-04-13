package com.ProdeMaster.PredictionService.domain.model;

import com.ProdeMaster.PredictionService.domain.exception.InvalidPredictionException;
import java.time.Instant;
import java.util.UUID;

/**
 * Aggregate root representing a user's prediction for a specific football
 * match.
 *
 * <p>
 * A prediction captures the <strong>exact scoreline</strong> a user believes
 * will
 * occur (e.g. Home 2 - 1 Away), modelled via the {@link MatchScore} embedded
 * value
 * object. The match outcome (home win / draw / away win) is always derived from
 * the score and never stored independently.
 *
 * <h3>Lifecycle</h3>
 * 
 * <pre>
 *   PENDING  ──lock()──►  LOCKED
 *   PENDING  ──void()──►  VOIDED
 *   LOCKED   ──void()──►  VOIDED
 * </pre>
 */
public class Prediction {

    private String id;
    private String userId;
    private String matchId;
    // The exact scoreline predicted by the user.
    private MatchScore predictedScore;
    private PredictionStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Prediction(String id, String userId, String matchId, MatchScore predictedScore) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.predictedScore = predictedScore;
        this.status = PredictionStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Factory method — the canonical way to create a new prediction.
     *
     * @param userId         identifier of the user making the prediction
     * @param matchId        identifier of the match being predicted
     * @param predictedScore exact scoreline the user is predicting (must not be
     *                       null)
     * @return a new {@code Prediction} in {@link PredictionStatus#PENDING} status
     * @throws InvalidPredictionException if {@code predictedScore} is null
     * @throws IllegalArgumentException   if {@code userId} or {@code matchId} is
     *                                    null or blank
     */
    public static Prediction create(String userId, String matchId, MatchScore predictedScore) {
        if (predictedScore == null) {
            throw new InvalidPredictionException("A predicted score must be provided");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must be provided");
        }
        if (matchId == null || matchId.isBlank()) {
            throw new IllegalArgumentException("Match ID must be provided");
        }
        return new Prediction(UUID.randomUUID().toString(), userId, matchId, predictedScore);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMatchId() {
        return matchId;
    }

    public MatchScore getPredictedScore() {
        return predictedScore;
    }

    public MatchOutcome getDerivedOutcome() {
        return predictedScore.deriveOutcome();
    }

    public PredictionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public boolean canModify() {
        return this.status == PredictionStatus.PENDING;
    }

    // -------------------------------------------------------------------------
    // Domain commands
    // -------------------------------------------------------------------------

    public void updateScore(MatchScore newScore) {
        if (newScore == null) {
            throw new InvalidPredictionException("New predicted score must not be null");
        }
        if (!canModify()) {
            throw new InvalidPredictionException(
                    "Cannot update a prediction that is not in PENDING status. Current status: " + this.status);
        }
        this.predictedScore = newScore;
        this.updatedAt = Instant.now();
    }

    public void lock() {
        if (this.status != PredictionStatus.PENDING) {
            throw new InvalidPredictionException(
                    "Only PENDING predictions can be locked. Current status: " + this.status);
        }
        this.status = PredictionStatus.LOCKED;
        // this.updatedAt = Instant.now();
    }

    /**
     * Voids this prediction, marking it as invalid (e.g. match cancelled or
     * postponed).
     * This operation is idempotent — calling it on an already-voided prediction has
     * no effect.
     *
     * @param reason human-readable explanation for why the prediction is being
     *               voided
     */
    public void voidPrediction(String reason) {
        if (this.status == PredictionStatus.VOIDED) {
            return; // idempotent
        }
        if (reason != null && !reason.isBlank()) {
            System.out.println("Reason for voiding: " + reason);
        }
        this.status = PredictionStatus.VOIDED;
        // this.updatedAt = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Guard methods (usable by application layer to fail fast before I/O)
    // -------------------------------------------------------------------------

    /**
     * Throws {@link InvalidPredictionException} if this prediction is LOCKED.
     */
    public void validateNotLocked() {
        if (this.status == PredictionStatus.LOCKED) {
            throw new InvalidPredictionException("Prediction is LOCKED and cannot be modified");
        }
    }

    /**
     * Throws {@link InvalidPredictionException} if this prediction is VOIDED.
     */
    public void validateNotVoided() {
        if (this.status == PredictionStatus.VOIDED) {
            throw new InvalidPredictionException("Prediction is VOIDED and cannot be modified");
        }
    }

    /**
     * Domain rule: validates that a given match status string permits new
     * predictions.
     *
     * <p>
     * This encapsulates the domain knowledge of which external match states are
     * acceptable for accepting predictions. The actual match info is fetched in the
     * application layer; this method only contains the rule logic.
     *
     * @param matchStatus the status string received from the MatchService
     * @throws InvalidPredictionException if the match is in a state that does not
     *                                    accept new predictions (e.g. started,
     *                                    finished, cancelled)
     */
    public static void validateMatchAcceptsPredictions(String matchStatus) {
        if (matchStatus == null || !isOpenStatus(matchStatus)) {
            throw new InvalidPredictionException(
                    "Cannot create or update a prediction for a match with status: " + matchStatus +
                            ". Match must be in a pre-match schedulable state.");
        }
    }

    /** Returns true if the given match status allows predictions to be placed. */
    private static boolean isOpenStatus(String matchStatus) {
        return switch (matchStatus.toUpperCase()) {
            case "PENDING", "NS", "SCHEDULED", "TBA" -> true;
            default -> false;
        };
    }
}