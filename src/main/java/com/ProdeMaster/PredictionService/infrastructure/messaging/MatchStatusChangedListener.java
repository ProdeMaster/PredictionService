package com.ProdeMaster.PredictionService.infrastructure.messaging;

import com.ProdeMaster.PredictionService.application.port.inbound.LockPredictionsForMatchInboundPort;
import com.ProdeMaster.PredictionService.application.port.inbound.VoidPredictionsForMatchInboundPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MatchStatusChangedListener {

    private static final Logger log = LoggerFactory.getLogger(MatchStatusChangedListener.class);

    private final LockPredictionsForMatchInboundPort lockPredictionsPort;
    private final VoidPredictionsForMatchInboundPort voidPredictionsPort;

    public MatchStatusChangedListener(
            LockPredictionsForMatchInboundPort lockPredictionsPort,
            VoidPredictionsForMatchInboundPort voidPredictionsPort) {
        this.lockPredictionsPort = lockPredictionsPort;
        this.voidPredictionsPort = voidPredictionsPort;
    }

    @KafkaListener(topics = "${app.kafka.topic.match-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleMatchStatusChanged(Map<String, Object> event) {
        log.info("Received match status changed event: {}", event);

        try {
            String matchId = (String) event.get("matchId");
            String newStatus = (String) event.get("newStatus");

            if (matchId == null || newStatus == null) {
                log.warn("Invalid event data: missing matchId or newStatus");
                return;
            }

            if (isMatchStarted(newStatus)) {
                log.info("Match {} started. Locking predictions.", matchId);
                lockPredictionsPort.lockPredictionsForMatch(matchId);
            } else if (isMatchCancelledOrPostponed(newStatus)) {
                log.info("Match {} cancelled or postponed. Voiding predictions.", matchId);
                voidPredictionsPort.voidPredictionsForMatch(matchId, "Match " + newStatus);
            }

        } catch (Exception e) {
            log.error("Error processing match status changed event: {}", e.getMessage(), e);
        }
    }

    private boolean isMatchStarted(String status) {
        return "IN_PLAY_1ST_HALF".equals(status) ||
               "IN_PLAY_2ND_HALF".equals(status) ||
               "HT".equals(status) ||
               "FT".equals(status);
    }

    private boolean isMatchCancelledOrPostponed(String status) {
        return "CANCELLED".equals(status) ||
               "POSTPONED".equals(status) ||
               "SUSPENDED".equals(status) ||
               "ABANDONED".equals(status);
    }
}