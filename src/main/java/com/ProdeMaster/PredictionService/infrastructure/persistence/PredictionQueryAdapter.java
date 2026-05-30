package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PredictionQueryAdapter implements PredictionQueryPort {

    private final PredictionRepository predictionRepository;

    public PredictionQueryAdapter(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @Override
    public Optional<Prediction> getById(String id) {
        return predictionRepository.findById(id);
    }

    @Override
    public Page<Prediction> getByMatchId(String matchId, Pageable pageable) {
        return predictionRepository.findByMatchId(matchId, pageable);
    }

    @Override
    public Page<Prediction> getByUserId(String userId, Pageable pageable) {
        return predictionRepository.findByUserId(userId, pageable);
    }

    @Override
    public Optional<Prediction> getByUserIdAndMatchId(String userId, String matchId) {
        return predictionRepository.findByUserIdAndMatchId(userId, matchId);
    }

    @Override
    public boolean existsByUserIdAndMatchId(String userId, String matchId) {
        return predictionRepository.findByUserIdAndMatchId(userId, matchId).isPresent();
    }

    @Override
    public Page<Prediction> getByUserIdAndGroupId(String userId, String groupId, Pageable pageable) {
        return predictionRepository.findByUserIdAndGroupId(userId, groupId, pageable);
    }
}