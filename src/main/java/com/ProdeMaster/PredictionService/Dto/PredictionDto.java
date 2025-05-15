package com.ProdeMaster.PredictionService.Dto;

import java.time.LocalDateTime;

public class PredictionDto {

    private Long id;
    private Long userId;
    private String matchId;
    private Integer scoreTeam1;
    private Integer scoreTeam2;
    private LocalDateTime createdAt;

    public PredictionDto(Long id, Long userId, String matchId, Integer scoreTeam1, Integer scoreTeam2, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.matchId = matchId;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMatchId() {
        return matchId;
    }

    public Integer getScoreTeam1() {
        return scoreTeam1;
    }

    public Integer getScoreTeam2() {
        return scoreTeam2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
