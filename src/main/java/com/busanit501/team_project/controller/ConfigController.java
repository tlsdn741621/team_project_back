package com.busanit501.team_project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Collections;

 @RestController @RequestMapping("/api/config")
public class ConfigController {

    // application.properties에서 키 값을 주입받습니다.
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    /**
     * React 애플리케이션에 Google Maps API 키를 JSON 형태로 제공합니다.
     * 엔드포인트: GET /api/config/google-key
     * 응답 형태: {"key": "AIzaSy..."}
     */
    @GetMapping("/google-key")
    public Map<String, String> getGoogleMapsKey() {
        // 실제 운영 환경에서는 키의 무분별한 노출을 막기 위해 
        // 인증/권한 확인 로직이 추가되어야 합니다.
        return Collections.singletonMap("key", googleMapsApiKey);
    }
}