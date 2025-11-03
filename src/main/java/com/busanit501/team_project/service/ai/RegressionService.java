package com.busanit501.team_project.service.ai;

import com.busanit501.team_project.dto.ai.RegressionRequestDTO;
import com.busanit501.team_project.dto.ai.RegressionResponseDTO;

import java.io.IOException;

public interface RegressionService {
    RegressionResponseDTO predict(RegressionRequestDTO requestDTO) throws IOException;
}
