package com.ProdeMaster.PredictionService.infrastructure.client;

import com.ProdeMaster.PredictionService.application.port.outbound.GroupServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stub implementation of {@link GroupServiceClient}.
 *
 * <p>
 * GroupService integration is not yet available. This class returns an empty
 * list and logs a warning until the real HTTP client is wired.
 * Replace the body of {@link #getGroupIdsByUserId(String)} once the
 * GroupService
 * contract and URL are defined.
 */

/*
 * TODO: Separar esta anotacion y llevarla a la capa de infraestructura para no
 * romper el principio de separacion de capas.
 */
@Component
public class GroupServiceClientImpl implements GroupServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceClientImpl.class);

    @Override
    public List<String> getGroupIdsByUserId(String userId) {
        // TODO: implement HTTP call to GroupService once available
        log.warn("GroupServiceClient not yet implemented — returning empty list for userId={}", userId);
        return List.of();
    }

    @Override
    public boolean isUserMemberOfGroup(String userId, String groupId) {
        // TODO: implement HTTP call to GroupService once available
        // Fail-closed: denying by default until the real client is wired.
        log.warn("GroupServiceClient.isUserMemberOfGroup not yet implemented — denying by default for userId={}, groupId={}", userId, groupId);
        return false;
    }
}
