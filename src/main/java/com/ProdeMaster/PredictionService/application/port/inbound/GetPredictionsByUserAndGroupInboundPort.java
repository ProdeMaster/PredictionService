package com.ProdeMaster.PredictionService.application.port.inbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Inbound port for retrieving a paginated list of predictions made by a specific
 * user within a specific group.
 *
 * <p>Implemented by: {@code GetPredictionsByUserAndGroupUseCase}.
 * Consumed by: the API adapter / controller layer.
 */
public interface GetPredictionsByUserAndGroupInboundPort {

    /**
     * Returns a paginated list of predictions for the given user and group.
     *
     * @param userId   ID of the user whose predictions to retrieve
     * @param groupId  ID of the group to filter by
     * @param pageable pagination and sort parameters
     * @return paginated predictions, never null
     */
    Page<Prediction> getByUserIdAndGroupId(String userId, String groupId, Pageable pageable);
}
