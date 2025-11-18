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
    public ResponseEntity<Map<String, Object>> predictTsunami( @RequestBody TsunamiPredictionRequest request) {
        try {
            // 1. Flask로부터 예측 결과를 받습니다.
            Map<String, Object> flaskResponse = predictionService.getTsunamiPrediction(request);

            // 2. 필요한 값을 추출합니다.
            Double tsunamiProbability = (Double) flaskResponse.get("tsunami_probability");
            Map<String, Object> features = (Map<String, Object>) flaskResponse.get("features");
            Integer isSteepSlope = (Integer) features.get("is_steep_slope");

            // 3. 프론트엔드로 보낼 새로운 응답 맵을 생성합니다. (Key를 camelCase로)
            Map<String, Object> responseToFrontend = new java.util.HashMap<>();
            responseToFrontend.put("tsunamiProbability", tsunamiProbability);
            responseToFrontend.put("isSteepSlope", isSteepSlope);

            // 4. 새로운 맵을 클라이언트에게 반환합니다.
            return ResponseEntity.ok(responseToFrontend);
        } catch (Exception e) {
            // 에러 로깅을 추가하는 것이 좋습니다.
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "예측 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
