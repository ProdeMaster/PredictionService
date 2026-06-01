package com.ProdeMaster.PredictionService.infrastructure.client;

import com.ProdeMaster.PredictionService.application.port.outbound.GroupServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class GroupServiceClientImpl implements GroupServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceClientImpl.class);

    private final WebClient webClient;

    public GroupServiceClientImpl(
            @LoadBalanced WebClient.Builder loadBalancedWebClientBuilder,
            @Value("${app.group-service.name}") String groupServiceName) {
        this.webClient = loadBalancedWebClientBuilder
                .baseUrl("lb://" + groupServiceName)
                .build();
    }

    @Override
    @CircuitBreaker(name = "groupService", fallbackMethod = "fallbackGetGroupIds")
    public List<String> getGroupIdsByUserId(String userId) {
        log.info("Fetching group IDs for userId={}", userId);
        return webClient.get()
                .uri("/api/v1/groups?userId={userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    @Override
    @CircuitBreaker(name = "groupService", fallbackMethod = "fallbackIsMember")
    public boolean isUserMemberOfGroup(String userId, String groupId) {
        log.info("Checking membership: userId={} in groupId={}", userId, groupId);
        return webClient.get()
                .uri("/api/v1/groups/{groupId}/members/{userId}", groupId, userId)
                .retrieve()
                .bodyToMono(MembershipResponse.class)
                .map(MembershipResponse::member)
                .block();
    }

    public List<String> fallbackGetGroupIds(String userId, Throwable t) {
        log.warn("GroupService unavailable — returning empty group list for userId={}. Reason: {}", userId, t.getMessage());
        return List.of();
    }

    public boolean fallbackIsMember(String userId, String groupId, Throwable t) {
        log.warn("GroupService unavailable — denying membership for userId={}, groupId={}. Reason: {}", userId, groupId, t.getMessage());
        return false;
    }

    private record MembershipResponse(boolean member) {}
}
