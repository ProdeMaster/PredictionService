package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.application.port.outbound.PredictionQueryPort;
import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Prediction> getByMatchId(String matchId) {
        return predictionRepository.findByMatchId(matchId);
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
}