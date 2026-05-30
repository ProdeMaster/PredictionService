package com.ProdeMaster.PredictionService.application.port.outbound;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository {
    Prediction save(Prediction prediction);

    Optional<Prediction> findById(String id);

    Optional<Prediction> findByUserIdAndMatchId(String userId, String matchId);

    Optional<Prediction> findByUserIdAndMatchIdAndGroupId(String userId, String matchId, String groupId);

    Page<Prediction> findByMatchId(String matchId, Pageable pageable);

    List<Prediction> findByMatchId(String matchId);

    Page<Prediction> findByUserId(String userId, Pageable pageable);

    List<Prediction> findByMatchIdAndStatus(String matchId, String status);

    Page<Prediction> findByUserIdAndGroupId(String userId, String groupId, Pageable pageable);

    void delete(Prediction prediction);
}