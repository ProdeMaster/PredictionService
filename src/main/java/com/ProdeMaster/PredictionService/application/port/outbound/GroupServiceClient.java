package com.ProdeMaster.PredictionService.application.port.outbound;

import java.util.List;

/**
 * Outbound port for querying group membership from the GroupService.
 *
 * <p>Used by {@code CreatePredictionUseCase} to resolve which groups a user
 * belongs to when {@code groupId} is not specified in the creation request.
 *
 * <p>Implemented by: {@code GroupServiceClientImpl} (infrastructure layer).
 */
public interface GroupServiceClient {

    /**
     * Returns the IDs of all groups the given user belongs to.
     *
     * @param userId the user whose group memberships to resolve
     * @return a non-null, possibly empty list of group IDs
     */
    List<String> getGroupIdsByUserId(String userId);

    /**
     * Returns whether the given user is a member of the given group.
     *
     * @param userId  the user to check
     * @param groupId the group to check membership in
     * @return {@code true} if the user belongs to the group
     */
    boolean isUserMemberOfGroup(String userId, String groupId);
}
