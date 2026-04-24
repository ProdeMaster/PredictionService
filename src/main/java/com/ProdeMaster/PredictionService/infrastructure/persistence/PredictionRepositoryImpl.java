package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PredictionRepositoryImpl implements PredictionRepository {

    private final PredictionJpaRepository predictionJpaRepository;

    public PredictionRepositoryImpl(PredictionJpaRepository predictionJpaRepository) {
        this.predictionJpaRepository = predictionJpaRepository;
    }

    @Override
    public Prediction save(Prediction prediction) {
        return predictionJpaRepository.save(prediction);
    }

    @Override
    public Optional<Prediction> findById(String id) {
        return predictionJpaRepository.findById(id);
    }

    @Override
    public Optional<Prediction> findByUserIdAndMatchId(String userId, String matchId) {
        return predictionJpaRepository.findByUserIdAndMatchId(userId, matchId);
    }

    @Override
    public List<Prediction> findByMatchId(String matchId) {
        return predictionJpaRepository.findByMatchId(matchId);
    }

    @Override
    public Page<Prediction> findByUserId(String userId, Pageable pageable) {
        return predictionJpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Prediction> findByMatchIdAndStatus(String matchId, String status) {
        return predictionJpaRepository.findByMatchIdAndStatus(matchId, status);
    }

    @Override
    public void delete(Prediction prediction) {
        predictionJpaRepository.delete(prediction);
    }
}