package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Inbound port for fetching a paginated list of predictions made by a specific
 * user.
 *
 * <p>
 * Implemented by: {@code GetPredictionsByUserUseCase}.
 * Consumed by: the API adapter / controller layer.
 *
 * <p>
 * <b>Note on Spring Data types:</b> {@code Page} and {@code Pageable} are
 * Spring
 * Data abstractions. They are intentionally accepted here as a practical
 * trade-off;
 * if a strict framework-free boundary is required in the future, a custom page
 * abstraction should replace them.
 */
public interface GetPredictionsByUserInboundPort {

    /**
     * Returns a paginated list of all predictions made by the given user.
     *
     * @param userId   ID of the user
     * @param pageable pagination and sorting parameters
     * @return paginated {@link Prediction} results
     */
    Page<Prediction> getByUserId(String userId, Pageable pageable);
}
