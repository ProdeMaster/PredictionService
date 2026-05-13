package com.ProdeMaster.PredictionService.api.controller;

import com.ProdeMaster.PredictionService.api.dto.CreatePredictionRequest;
import com.ProdeMaster.PredictionService.api.dto.PredictionResponse;
import com.ProdeMaster.PredictionService.api.dto.UpdatePredictionRequest;
import com.ProdeMaster.PredictionService.application.port.inbound.CancelPredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.CreatePredictionInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionByIdInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserAndGroupInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.GetPredictionsByUserInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.UpdatePredictionInboundPort;
import com.ProdeMaster.PredictionService.api.exception.MissingUserHeaderException;
import jakarta.validation.Valid;
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

    private final CreatePredictionInboundPort createPredictionPort;
    private final UpdatePredictionInboundPort updatePredictionPort;
    private final CancelPredictionInboundPort cancelPredictionPort;
    private final GetPredictionByIdInboundPort getPredictionByIdPort;
    private final GetPredictionsByMatchInboundPort getPredictionsByMatchPort;
    private final GetPredictionsByUserInboundPort getPredictionsByUserPort;
    private final GetPredictionsByUserAndGroupInboundPort getPredictionsByUserAndGroupPort;

    public PredictionController(
            CreatePredictionInboundPort createPredictionPort,
            UpdatePredictionInboundPort updatePredictionPort,
            CancelPredictionInboundPort cancelPredictionPort,
            GetPredictionByIdInboundPort getPredictionByIdPort,
            GetPredictionsByMatchInboundPort getPredictionsByMatchPort,
            GetPredictionsByUserInboundPort getPredictionsByUserPort,
            GetPredictionsByUserAndGroupInboundPort getPredictionsByUserAndGroupPort) {
        this.createPredictionPort = createPredictionPort;
        this.updatePredictionPort = updatePredictionPort;
        this.cancelPredictionPort = cancelPredictionPort;
        this.getPredictionByIdPort = getPredictionByIdPort;
        this.getPredictionsByMatchPort = getPredictionsByMatchPort;
        this.getPredictionsByUserPort = getPredictionsByUserPort;
        this.getPredictionsByUserAndGroupPort = getPredictionsByUserAndGroupPort;
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
    public ResponseEntity<List<PredictionResponse>> getPredictionsByMatch(
            @PathVariable String matchId) {
        var predictions = getPredictionsByMatchPort.getByMatchId(matchId);
        return ResponseEntity.ok(predictions.stream()
                .map(PredictionResponse::fromDomain)
                .toList());
    }

    // Method to be able to view another user's predictions.
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUser(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireGatewayHeader(gatewayUserId);
        var predictions = getPredictionsByUserPort.getByUserId(
                userId,
                PageRequest.of(page, size));
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }

    // Method to be able to view another user's predictions in a specific group.
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUserAndGroup(
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireGatewayHeader(gatewayUserId);
        var predictions = getPredictionsByUserAndGroupPort.getByUserIdAndGroupId(
                gatewayUserId,
                groupId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
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