package com.ProdeMaster.PredictionService.Service.Client.Dto;

import java.time.LocalDateTime;

//Match deadline
public class DeadlineDto {
    private final Long MatchID;
    private final LocalDateTime deadLine;

    public DeadlineDto(Long MatchID, LocalDateTime deadLine) {
        this.MatchID = MatchID;
        this.deadLine = deadLine;
        }

    public Long getMatchID() {
        return MatchID;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }
}
