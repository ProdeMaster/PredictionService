package com.ProdeMaster.PredictionService.application.port.outbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Outbound port for reading {@link Prediction} data from persistence.
 *
 * <p>This is a <strong>query-side</strong> outbound port (driven port). It is
 * implemented in the infrastructure layer (e.g. {@code PredictionQueryAdapter})
 * and injected into query use cases by the dependency-injection container.
 *
 * <p>It is deliberately kept separate from {@link PredictionRepository} to honour
 * a lightweight Command / Query separation: command use cases write via
 * {@code PredictionRepository}; query use cases read via this port.
 */
public interface PredictionQueryPort {

    /**
     * Finds a prediction by its unique ID.
     *
     * @param id prediction ID
     * @return an {@link Optional} containing the prediction, or empty if not found
     */
    Optional<Prediction> getById(String id);

    /**
     * Returns a paginated list of predictions for the given match.
     *
     * @param matchId match ID
     * @param pageable pagination parameters
     * @return paginated predictions
     */
    Page<Prediction> getByMatchId(String matchId, Pageable pageable);

    /**
     * Returns a paginated list of a user's predictions.
     *
     * @param userId   user ID
     * @param pageable pagination parameters
     * @return paginated predictions
     */
    Page<Prediction> getByUserId(String userId, Pageable pageable);

    /**
     * Finds a specific prediction by user and match.
     *
     * @param userId  user ID
     * @param matchId match ID
     * @return an {@link Optional} containing the prediction, or empty if not found
     */
    Optional<Prediction> getByUserIdAndMatchId(String userId, String matchId);

    /**
     * Returns whether a prediction exists for the given user and match combination.
     *
     * @param userId  user ID
     * @param matchId match ID
     * @return {@code true} if a prediction already exists
     */
    boolean existsByUserIdAndMatchId(String userId, String matchId);

    /**
     * Returns a paginated list of predictions for the given user and group.
     *
     * @param userId   user ID
     * @param groupId  group ID
     * @param pageable pagination and sort parameters
     * @return paginated predictions, never null
     */
    Page<Prediction> getByUserIdAndGroupId(String userId, String groupId, Pageable pageable);
}
