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
            @Valid @RequestBody CreatePredictionRequest request) {
        var predictions = createPredictionPort.create(
                request.userId(),
                request.matchId(),
                request.homeTeamGoals(),
                request.awayTeamGoals(),
                request.groupId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(predictions.stream().map(PredictionResponse::fromDomain).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PredictionResponse> updatePrediction(
            @PathVariable String id,
            @Valid @RequestBody UpdatePredictionRequest request) {
        var prediction = updatePredictionPort.update(
                id,
                request.homeTeamGoals(),
                request.awayTeamGoals());
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PredictionResponse> cancelPrediction(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "User cancelled") String reason) {
        var prediction = cancelPredictionPort.cancel(id, reason);
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PredictionResponse> getPredictionById(@PathVariable String id) {
        var prediction = getPredictionByIdPort.getById(id);
        return ResponseEntity.ok(PredictionResponse.fromDomain(prediction));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<PredictionResponse>> getPredictionsByMatch(
            @PathVariable String matchId) {
        var predictions = getPredictionsByMatchPort.getByMatchId(matchId);
        return ResponseEntity.ok(predictions.stream()
                .map(PredictionResponse::fromDomain)
                .toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var predictions = getPredictionsByUserPort.getByUserId(
                userId,
                PageRequest.of(page, size));
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }

    // GET /api/v1/predictions/user/{userId}/group/{groupId}?page=0&size=20
    // Defaults to createdAt DESC. Will switch to matchScheduledAt once P9 adds that field to Prediction.
    @GetMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Page<PredictionResponse>> getPredictionsByUserAndGroup(
            @PathVariable String userId,
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var predictions = getPredictionsByUserAndGroupPort.getByUserIdAndGroupId(
                userId,
                groupId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(predictions.map(PredictionResponse::fromDomain));
    }
}