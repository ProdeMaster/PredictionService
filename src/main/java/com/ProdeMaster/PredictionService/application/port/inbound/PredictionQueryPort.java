package com.ProdeMaster.PredictionService.application.port.inbound;

/**
 * @deprecated This interface was incorrectly placed in the {@code inbound} package.
 * Query ports are <strong>outbound</strong> ports (the application calls out to persistence).
 *
 * <p>It has been relocated to:
 * {@link com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort}
 *
 * <p><strong>This file must be deleted once the infrastructure layer
 * ({@code PredictionQueryAdapter}) no longer references it.</strong>
 */
@Deprecated(since = "application-refactor", forRemoval = true)
public interface PredictionQueryPort {
    // Intentionally empty — use the outbound version instead.
}