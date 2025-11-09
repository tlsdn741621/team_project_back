package com.busanit501.team_project.controller;

import com.busanit501.team_project.service.EarthquakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/earthquake")
@RequiredArgsConstructor
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    @GetMapping("/realtime")
    public ResponseEntity<String> getRealtimeEarthquakeData() {
        try {
            String data = earthquakeService.getEarthquakeData();
            return ResponseEntity.ok(data);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching earthquake data: " + e.getMessage());
        }
    }
}
