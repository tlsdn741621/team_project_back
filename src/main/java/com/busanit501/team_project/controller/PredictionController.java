package com.busanit501.team_project.controller;

import com.busanit501.team_project.dto.TsunamiPredictionRequest;
import com.busanit501.team_project.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/tsunami")
    public ResponseEntity<Map<String, Object>> predictTsunami(@RequestBody TsunamiPredictionRequest request) {
        try {
            Map<String, Object> result = predictionService.getTsunamiPrediction(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "예측 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
