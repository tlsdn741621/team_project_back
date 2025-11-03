package com.busanit501.team_project.service.ai;

import com.busanit501.team_project.dto.ai.RegressionRequestDTO;
import com.busanit501.team_project.dto.ai.RegressionResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class RegressionServiceImpl implements RegressionService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.flask.server.url}")
    private String flaskServerUrl;

    @Override
    public RegressionResponseDTO predict(RegressionRequestDTO requestDTO) throws IOException {
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        String flaskEndpoint = "/predict/regression";

        Request request = new Request.Builder()
                .url(flaskServerUrl + flaskEndpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Flask server error: {}", response.body().string());
                throw new IOException("Unexpected response code: " + response);
            }
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, RegressionResponseDTO.class);
        }
    }

}
