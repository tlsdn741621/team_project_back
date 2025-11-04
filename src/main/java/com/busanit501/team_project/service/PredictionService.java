package com.busanit501.team_project.service;

import com.busanit501.team_project.dto.TsunamiPredictionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PredictionService {

    private final RestTemplate restTemplate;

    @Value("${ai.flask.server.url}")
    private String flaskServerUrl;

    @Autowired
    public PredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getTsunamiPrediction(TsunamiPredictionRequest request) {
        String endpoint = "/predict";
        String url = flaskServerUrl + endpoint;

        return restTemplate.postForObject(url, request, Map.class);
    }
}
