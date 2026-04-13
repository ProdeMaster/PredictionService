package com.ProdeMaster.PredictionService.application.usecase;

import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Application use case: retrieve a paginated list of predictions made by a specific user.
 *
 * <h3>Hexagonal Architecture notes</h3>
 * <ul>
 *   <li>No {@code @Service} annotation — pure Java, wired by an infrastructure
 *       {@code @Configuration}.</li>
 *   <li>Depends on {@link PredictionQueryPort} (outbound) for reads.</li>
 *   <li>{@code Page} and {@code Pageable} are Spring Data types accepted as a pragmatic
 *       trade-off. If a strict framework-free boundary is needed, a custom page
 *       abstraction should be introduced at that time.</li>
 * </ul>
 */
public class GetPredictionsByUserUseCase implements GetPredictionsByUserInboundPort {

    private final PredictionQueryPort predictionQueryPort;

    public GetPredictionsByUserUseCase(PredictionQueryPort predictionQueryPort) {
        this.predictionQueryPort = predictionQueryPort;
    }

    @Override
    public Page<Prediction> getByUserId(String userId, Pageable pageable) {
        return predictionQueryPort.getByUserId(userId, pageable);
    }
}