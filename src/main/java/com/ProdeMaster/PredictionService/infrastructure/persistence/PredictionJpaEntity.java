package com.ProdeMaster.PredictionService.infrastructure.persistence;

import com.ProdeMaster.PredictionService.domain.model.PredictionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "predictions")
public class PredictionJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String matchId;

    private String groupId;

    @Embedded
    private MatchScoreEmbeddable predictedScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PredictionStatus status;

    private Instant matchScheduledAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected PredictionJpaEntity() {
    }

    public PredictionJpaEntity(String id, String userId, String matchId, String groupId,
                               MatchScoreEmbeddable predictedScore, PredictionStatus status,
                               Instant matchScheduledAt, Instant createdAt, Instant updatedAt,
                               Long version) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.groupId = groupId;
        this.predictedScore = predictedScore;
        this.status = status;
        this.matchScheduledAt = matchScheduledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public MatchScoreEmbeddable getPredictedScore() { return predictedScore; }
    public void setPredictedScore(MatchScoreEmbeddable predictedScore) { this.predictedScore = predictedScore; }

    public PredictionStatus getStatus() { return status; }
    public void setStatus(PredictionStatus status) { this.status = status; }

    public Instant getMatchScheduledAt() { return matchScheduledAt; }
    public void setMatchScheduledAt(Instant matchScheduledAt) { this.matchScheduledAt = matchScheduledAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
