package com.ProdeMaster.PredictionService.Service.Client.Dto;

import java.time.LocalDateTime;

public class MatchDto {

    private final String id;
    private final String team1;
    private final String team2;
    private final LocalDateTime deadLine;
    private final Integer resultTeam1;
    private final Integer resultTeam2;


    public MatchDto(String id, String team1, String team2, LocalDateTime deadLine, Integer resultTeam1, Integer resultTeam2) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.deadLine = deadLine;
        this.resultTeam1 = resultTeam1;
        this.resultTeam2 = resultTeam2;
    }

    public String getId() {
        return id;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public Integer getResultTeam1() {
        return resultTeam1;
    }

    public Integer getResultTeam2() {
        return resultTeam2;
    }
}
