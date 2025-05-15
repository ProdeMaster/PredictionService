package com.ProdeMaster.PredictionService.Service.Client;

import com.ProdeMaster.PredictionService.Service.Client.Dto.DeadlineDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class MatchService {
    public static DeadlineDto getDeadLine (String id) {
        RestClient restClient = RestClient.builder().baseUrl("http://match-service/deadline").build();
        String uri = "/"+id;
        return restClient.get().uri(uri).retrieve().body(DeadlineDto.class);
    }
}
