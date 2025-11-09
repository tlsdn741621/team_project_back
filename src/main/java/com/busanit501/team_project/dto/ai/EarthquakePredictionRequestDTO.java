package com.busanit501.team_project.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarthquakePredictionRequestDTO {
    private Double magnitude;
    private Double latitude;
    private Double longitude;
    private Double depth;
}
