package com.busanit501.team_project.service;

import com.busanit501.team_project.dto.ai.EarthquakePredictionRequestDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EarthquakeServiceImpl implements EarthquakeService {

    private static final String USGS_API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=%s&minmagnitude=4.0";
    private static final String FLASK_API_URL = "http://localhost:5000/predict";

    @Override
    public String getEarthquakeData() throws IOException, InterruptedException {
        String oneHourAgo = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
//        String apiUrl = String.format(USGS_API_URL, yesterday);
        String apiUrl = String.format(USGS_API_URL, oneHourAgo);
        log.info("USGS API URL: {}", apiUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        log.debug("USGS API 원본 응답: {}", responseBody);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

        if (jsonObject.has("features") && jsonObject.get("features").getAsJsonArray().size() > 0) {
            JsonArray features = jsonObject.getAsJsonArray("features");
            log.info("USGS로부터 {}개의 지진 특성 데이터를 수신했습니다.", features.size());
            List<EarthquakePredictionRequestDTO> predictionRequestDTOs = new ArrayList<>();

            for (JsonElement featureElement : features) {
                JsonObject feature = featureElement.getAsJsonObject();
                JsonObject properties = feature.getAsJsonObject("properties");
                JsonObject geometry = feature.getAsJsonObject("geometry");

                if (properties != null && geometry != null && properties.has("mag")) {
                    Double magnitude = properties.get("mag").getAsDouble();

                    JsonArray coordinates = geometry.getAsJsonArray("coordinates");
                    if (coordinates != null && coordinates.size() >= 3) {
                        Double longitude = coordinates.get(0).getAsDouble();
                        Double latitude = coordinates.get(1).getAsDouble();
                        Double depth = coordinates.get(2).getAsDouble();

                        predictionRequestDTOs.add(new EarthquakePredictionRequestDTO(magnitude, latitude, longitude, depth));
                    }
                }
            }

            String dtoListJson = gson.toJson(predictionRequestDTOs);
            log.info("Flask 서버로 {}개의 DTO를 전송합니다. JSON: {}", predictionRequestDTOs.size(), dtoListJson);

            HttpRequest flaskRequest = HttpRequest.newBuilder()
                    .uri(URI.create(FLASK_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(dtoListJson))
                    .build();

            HttpResponse<String> flaskResponse = client.send(flaskRequest, HttpResponse.BodyHandlers.ofString());
            log.info("Flask 서버로부터 응답 수신. 상태: {}, 본문: {}", flaskResponse.statusCode(), flaskResponse.body());
            return flaskResponse.body();
        } else {
            log.info("USGS로부터 유의미한 지진 데이터를 찾을 수 없습니다. 샘플 데이터를 사용합니다.");
            List<EarthquakePredictionRequestDTO> sampleData = getSampleEarthquakeDataForDemo();
            String dtoListJson = gson.toJson(sampleData);
            log.info("Flask 서버로 {}개의 샘플 DTO를 전송합니다. JSON: {}", sampleData.size(), dtoListJson);

            HttpRequest flaskRequest = HttpRequest.newBuilder()
                    .uri(URI.create(FLASK_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(dtoListJson))
                    .build();

            HttpResponse<String> flaskResponse = client.send(flaskRequest, HttpResponse.BodyHandlers.ofString());
            log.info("Flask 서버로부터 샘플 데이터에 대한 응답 수신. 상태: {}, 본문: {}", flaskResponse.statusCode(), flaskResponse.body());
            return flaskResponse.body();
        }
    }

    private List<EarthquakePredictionRequestDTO> getSampleEarthquakeDataForDemo() {
        List<EarthquakePredictionRequestDTO> sampleData = new ArrayList<>();
        // 샘플 데이터 1: 일본 근처
        sampleData.add(new EarthquakePredictionRequestDTO(5.5, 35.6895, 139.6917, 10.0));
        // 샘플 데이터 2: 대만 근처
        sampleData.add(new EarthquakePredictionRequestDTO(6.2, 23.6978, 120.9605, 20.0));
        // 샘플 데이터 3: 필리핀 근처
        sampleData.add(new EarthquakePredictionRequestDTO(5.8, 12.8797, 121.7740, 15.0));
        return sampleData;
    }
}
