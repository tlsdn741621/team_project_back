package com.busanit501.team_project.dto;

import lombok.Data;

@Data
public class TsunamiPredictionRequest {
    private Double latitude;
    private Double longitude;
    private Double magnitude;
    private Integer depth;
}
