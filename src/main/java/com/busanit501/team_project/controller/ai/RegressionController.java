package com.busanit501.team_project.controller.ai;

import com.busanit501.team_project.dto.ai.RegressionRequestDTO;
import com.busanit501.team_project.dto.ai.RegressionResponseDTO;
import com.busanit501.team_project.service.ai.RegressionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/regression")
@RequiredArgsConstructor
@Log4j2

public class RegressionController {
    private final RegressionService regressionService;
    @PostMapping("/predict")
    public ResponseEntity<RegressionResponseDTO> predict(
            @RequestBody RegressionRequestDTO requestDTO) throws IOException {
        log.info("Regression predict request: {}", requestDTO);
        RegressionResponseDTO responseDTO = regressionService.predict(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
