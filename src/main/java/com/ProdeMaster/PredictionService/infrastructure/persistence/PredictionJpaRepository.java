package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface PredictionJpaRepository extends JpaRepository<Prediction, String> {

    Optional<Prediction> findByUserIdAndMatchId(String userId, String matchId);

    Optional<Prediction> findByUserIdAndMatchIdAndGroupId(String userId, String matchId, String groupId);

    List<Prediction> findByMatchId(String matchId);

    Page<Prediction> findByUserId(String userId, Pageable pageable);

    @Query("SELECT p FROM Prediction p WHERE p.matchId = :matchId AND p.status = :status")
    List<Prediction> findByMatchIdAndStatus(@Param("matchId") String matchId, @Param("status") String status);

    Page<Prediction> findByUserIdAndGroupId(String userId, String groupId, Pageable pageable);
}