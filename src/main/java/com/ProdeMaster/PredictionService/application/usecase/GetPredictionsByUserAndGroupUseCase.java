package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserAndGroupInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Application use case: retrieve a paginated list of predictions made by a specific
 * user within a specific group.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No {@code @Service} annotation — pure Java, wired by an infrastructure
 *       {@code @Configuration}.</li>
 *   <li>Depends on {@link PredictionQueryPort} (outbound) for reads.</li>
 *   <li>Sort order is {@code matchScheduledAt ASC}, passed via {@link Pageable} from the controller.
 *       The field is denormalized onto {@link com.ProdeMaster.PredictionService.domain.model.Prediction}
 *       from the MatchService at creation time.</li>
 * </ul>
 */
public class GetPredictionsByUserAndGroupUseCase implements GetPredictionsByUserAndGroupInboundPort {

    private final PredictionQueryPort predictionQueryPort;

    public GetPredictionsByUserAndGroupUseCase(PredictionQueryPort predictionQueryPort) {
        this.predictionQueryPort = predictionQueryPort;
    }

    @Override
    public Page<Prediction> getByUserIdAndGroupId(String userId, String groupId, Pageable pageable) {
        return predictionQueryPort.getByUserIdAndGroupId(userId, groupId, pageable);
    }
}
