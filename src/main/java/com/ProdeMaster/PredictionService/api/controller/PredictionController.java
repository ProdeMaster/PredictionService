package com.ProdeMaster.PredictionService.api.controller;

import com.ProdeMaster.PredictionService.api.dto.CreatePredictionRequest;
import com.ProdeMaster.PredictionService.api.dto.PredictionResponse;
import com.ProdeMaster.PredictionService.api.dto.UpdatePredictionRequest;
import com.ProdeMaster.PredictionService.api.exception.ForbiddenAccessException;
import com.ProdeMaster.PredictionService.api.exception.MissingUserHeaderException;
import com.ProdeMaster.PredictionService.api.exception.UserNotInGroupException;
import com.ProdeMaster.PredictionService.application.port.inbound.CancelPredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.CreatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionByIdInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserAndGroupInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.UpdatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.outbound.GroupServiceClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/predictions")
public class PredictionController {

    private static final Logger log = LoggerFactory.getLogger(PredictionController.class);

    private final CreatePredictionInboundPort createPredictionPort;
    private final UpdatePredictionInboundPort updatePredictionPort;
    private final CancelPredictionInboundPort cancelPredictionPort;
    private final GetPredictionByIdInboundPort getPredictionByIdPort;
    private final GetPredictionsByMatchInboundPort getPredictionsByMatchPort;
    private final GetPredictionsByUserInboundPort getPredictionsByUserPort;
    private final GetPredictionsByUserAndGroupInboundPort getPredictionsByUserAndGroupPort;
    private final GroupServiceClient groupServiceClient;

    public PredictionController(
            CreatePredictionInboundPort createPredictionPort,
            UpdatePredictionInboundPort updatePredictionPort,
            CancelPredictionInboundPort cancelPredictionPort,
            GetPredictionByIdInboundPort getPredictionByIdPort,
            GetPredictionsByMatchInboundPort getPredictionsByMatchPort,
            GetPredictionsByUserInboundPort getPredictionsByUserPort,
            GetPredictionsByUserAndGroupInboundPort getPredictionsByUserAndGroupPort,
            GroupServiceClient groupServiceClient) {
        this.createPredictionPort = createPredictionPort;
        this.updatePredictionPort = updatePredictionPort;
        this.cancelPredictionPort = cancelPredictionPort;
        this.getPredictionByIdPort = getPredictionByIdPort;
        this.getPredictionsByMatchPort = getPredictionsByMatchPort;
        this.getPredictionsByUserPort = getPredictionsByUserPort;
        this.getPredictionsByUserAndGroupPort = getPredictionsByUserAndGroupPort;
        this.groupServiceClient = groupServiceClient;
    }

    @PostMapping
    public ResponseEntity<List<PredictionResponse>> createPrediction(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @Valid @RequestBody CreatePredictionRequest request) {
        requireGatewayHeader(gatewayUserId);
        var predictions = createPredictionPort.create(
                gatewayUserId,
                request.matchId(),
                request.homeTeamGoals(),
                request.awayTeamGoals(),
                request.groupId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(predictions.stream().map(PredictionResponse::fromDomain).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PredictionResponse> updatePrediction(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String id,
            @Valid @RequestBody UpdatePredictionRequest request) {
        requireGatewayHeader(gatewayUserId);
        var prediction = updatePredictionPort.update(
                id,
                request.homeTeamGoals(),
                request.awayTeamGoals());
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PredictionResponse> cancelPrediction(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "User cancelled") String reason) {
        requireGatewayHeader(gatewayUserId);
        var prediction = cancelPredictionPort.cancel(id, reason);
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PredictionResponse> getPredictionById(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String id) {
        requireGatewayHeader(gatewayUserId);
        var prediction = getPredictionByIdPort.getById(id);
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    // Public — no authentication required (querying a match's predictions is open)
    @GetMapping("/match/{matchId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByMatch(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String matchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        /**
         * TODO: Replace the getByMatchId code with getByMatchIdAndSharedGroup
         * This new query should take into account that a user querying about the
         * predictions
         * for a match will only see the predictions from the groups they share
         * with the first. This means that if user A wants to know the results predicted
         * for a match, they will only be able to see the results from
         * the predictions that are shared in the same group as user A.
         * Example:
         * User A -> belongs to groups: "World Cup with friends", "La Liga with family",
         * "La Liga Hypermotion"
         * User B -> belongs to groups: "World Cup with friends", "La Liga with family",
         * "La Liga with friends", "Serie A for honor"
         * User A can only see the predictions that User B made about matches in the
         * group: "World Cup with friends", "La Liga with family"
         */
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var predictions = getPredictionsByMatchPort.getByMatchId(matchId, pageable);
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }

    // Method to be able to view another user's predictions.
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUser(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireGatewayHeader(gatewayUserId);
        /**
         * TODO: Replace the getByUserId code with getByUserIdAndSharedGroup
         * This new query should take into account that a user querying another user's
         * results will only see the second user's results in the groups they share
         * with the first. This means that if user A wants to know the results predicted
         * by user B, they will only be able to see the results from user B that are
         * shared in the same group as user A.
         * Example:
         * User A -> belongs to groups: "World Cup with friends", "La Liga with family",
         * "La Liga Hypermotion"
         * User B -> belongs to groups: "World Cup with friends", "La Liga with family",
         * "La Liga with friends", "Serie A for honor"
         * User A can only see the predictions that User B made about matches in the
         * group: "World Cup with friends", "La Liga with family"
         */
        var predictions = getPredictionsByUserPort.getByUserId(
                userId,
                PageRequest.of(page, size));
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }

    // Retrieve predictions for any user within a specific group.
    // The authenticated user (X-User-Id) may query another user's predictions only if they share the group.
    @GetMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUserAndGroup(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String userId,
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireGatewayHeader(gatewayUserId);

        // Filter 2: authenticated user must belong to the group
        if (!groupServiceClient.isUserMemberOfGroup(gatewayUserId, groupId)) {
            throw new ForbiddenAccessException(
                    "User " + gatewayUserId + " is not a member of group " + groupId);
        }

        // Filter 3: queried user must belong to the group (skip when self-querying)
        if (!userId.equals(gatewayUserId)) {
            log.warn("User {} is requesting predictions of user {} in group {}", gatewayUserId, userId, groupId);
            if (!groupServiceClient.isUserMemberOfGroup(userId, groupId)) {
                throw new UserNotInGroupException(
                        "User " + userId + " is not a member of group " + groupId);
            }
        }

        var predictions = getPredictionsByUserAndGroupPort.getByUserIdAndGroupId(
                userId,
                groupId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "matchScheduledAt")));
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }

    // -------------------------------------------------------------------------
    // Gateway identity helpers
    // -------------------------------------------------------------------------

    private void requireGatewayHeader(String gatewayUserId) {
        if (gatewayUserId == null || gatewayUserId.isBlank()) {
            throw new MissingUserHeaderException(
                    "Required header 'X-User-Id' is missing or empty. Ensure the request passes through the API Gateway.");
        }
    }
}