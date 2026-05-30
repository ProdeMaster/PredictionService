package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.application.port.outbound.PredictionRepository;
import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PredictionRepositoryImpl implements PredictionRepository {

    private final PredictionJpaRepository predictionJpaRepository;

    public PredictionRepositoryImpl(PredictionJpaRepository predictionJpaRepository) {
        this.predictionJpaRepository = predictionJpaRepository;
    }

    @Override
    public Prediction save(Prediction prediction) {
        PredictionJpaEntity entity = PredictionMapper.toEntity(prediction);
        PredictionJpaEntity saved = predictionJpaRepository.save(entity);
        return PredictionMapper.toDomain(saved);
    }

    @Override
    public Optional<Prediction> findById(String id) {
        return predictionJpaRepository.findById(id)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public Optional<Prediction> findByUserIdAndMatchId(String userId, String matchId) {
        return predictionJpaRepository.findByUserIdAndMatchId(userId, matchId)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public Optional<Prediction> findByUserIdAndMatchIdAndGroupId(String userId, String matchId, String groupId) {
        return predictionJpaRepository.findByUserIdAndMatchIdAndGroupId(userId, matchId, groupId)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public List<Prediction> findByMatchId(String matchId) {
        return predictionJpaRepository.findByMatchId(matchId).stream()
                .map(PredictionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Prediction> findByMatchId(String matchId, Pageable pageable) {
        return predictionJpaRepository.findByMatchId(matchId, pageable)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public Page<Prediction> findByUserId(String userId, Pageable pageable) {
        return predictionJpaRepository.findByUserId(userId, pageable)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public List<Prediction> findByMatchIdAndStatus(String matchId, String status) {
        return predictionJpaRepository.findByMatchIdAndStatus(matchId, status).stream()
                .map(PredictionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Prediction> findByUserIdAndGroupId(String userId, String groupId, Pageable pageable) {
        return predictionJpaRepository.findByUserIdAndGroupId(userId, groupId, pageable)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public void delete(Prediction prediction) {
        PredictionJpaEntity entity = PredictionMapper.toEntity(prediction);
        predictionJpaRepository.delete(entity);
    }
}