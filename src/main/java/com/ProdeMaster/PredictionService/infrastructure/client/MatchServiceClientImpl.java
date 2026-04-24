package com.ProdeMaster.PredictionService.infrastructure.client;

import com.ProdeMaster.PredictionService.application.port.outbound.MatchServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Component
public class MatchServiceClientImpl implements MatchServiceClient {

    private final WebClient webClient;

    public MatchServiceClientImpl(
            @Value("${app.match-service.url}") String matchServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(matchServiceUrl)
                .build();
    }

    // TODO: CHANGE THE METHOD getMatch to getMatchById in MatchServiceClient and in
    // this method
    @Override
    @CircuitBreaker(name = "matchService", fallbackMethod = "fallbackGetMatch")
    public MatchInfo getMatch(String matchId) {
        MatchResponse response = webClient.get()
                .uri("/{id}", matchId)
                .retrieve()
                .bodyToMono(MatchResponse.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Empty response from Match Service");
        }

        return new MatchInfo(
                response.id(),
                response.homeTeam() != null ? response.homeTeam().name() : null,
                response.awayTeam() != null ? response.awayTeam().name() : null,
                response.scheduledAt(),
                response.status());
    }

    @Override
    @CircuitBreaker(name = "matchService", fallbackMethod = "fallbackGetMatchStatus")
    public MatchStatus getMatchStatus(String matchId) {
        MatchResponse response = webClient.get()
                .uri("/{id}", matchId)
                .retrieve()
                .bodyToMono(MatchResponse.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Empty response from Match Service");
        }

        return new MatchStatus(
                response.id(),
                response.status(),
                response.scheduledAt());
    }

    public MatchInfo fallbackGetMatch(String matchId, Throwable t) {
        throw new RuntimeException("Match Service unavailable: " + t.getMessage());
    }

    public MatchStatus fallbackGetMatchStatus(String matchId, Throwable t) {
        throw new RuntimeException("Match Service unavailable: " + t.getMessage());
    }

    record MatchResponse(
            String id,
            TeamResponse homeTeam,
            TeamResponse awayTeam,
            Instant scheduledAt,
            String status) {
    }

    record TeamResponse(
            String name) {
    }
}