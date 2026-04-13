package com.ProdeMaster.PredictionService.application.port.outbound;

import java.time.Instant;

public interface MatchServiceClient {

    MatchInfo getMatch(String matchId);

    MatchStatus getMatchStatus(String matchId);

    record MatchInfo(
        String id,
        String homeTeam,
        String awayTeam,
        Instant scheduledAt,
        String status
    ) {}

    record MatchStatus(
        String matchId,
        String status,
        Instant scheduledAt
    ) {}
}