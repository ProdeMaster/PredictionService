package com.ProdeMaster.PredictionService.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface PredictionJpaRepository extends JpaRepository<PredictionJpaEntity, String> {

    Optional<PredictionJpaEntity> findByUserIdAndMatchId(String userId, String matchId);

    Optional<PredictionJpaEntity> findByUserIdAndMatchIdAndGroupId(String userId, String matchId, String groupId);

    List<PredictionJpaEntity> findByMatchId(String matchId);

    Page<PredictionJpaEntity> findByMatchId(String matchId, Pageable pageable);

    Page<PredictionJpaEntity> findByUserId(String userId, Pageable pageable);

    @Query("SELECT p FROM PredictionJpaEntity p WHERE p.matchId = :matchId AND p.status = :status")
    List<PredictionJpaEntity> findByMatchIdAndStatus(@Param("matchId") String matchId, @Param("status") String status);

    Page<PredictionJpaEntity> findByUserIdAndGroupId(String userId, String groupId, Pageable pageable);
}