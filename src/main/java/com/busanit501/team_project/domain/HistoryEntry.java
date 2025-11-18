package com.busanit501.team_project.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_entries") // 데이터베이스 테이블명
public class HistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프런트엔드에서 넘어오는 필드들
    private String startDate; // YYYY-MM-DD 형식
    private Double minMagnitude;
    private Integer depth; // 깊이는 정수로 가정
    private String northCoord; // 위도 (문자열로 처리)
    private String westCoord;  // 경도 (문자열로 처리)
    private String predictionResult; // 예측 결과 (예: "XX.XX%")

    // 추가 필드 (선택 사항)
    private String memberId; // 사용자 ID (인증 구현 시 필요)

    private LocalDateTime createdAt; // 기록 생성 시간

    // 생성자
    public HistoryEntry() {
        this.createdAt = LocalDateTime.now(); // 기본값으로 현재 시간 설정
    }

    public HistoryEntry(String startDate, Double minMagnitude, Integer depth, String northCoord, String westCoord, String predictionResult) {
        this.startDate = startDate;
        this.minMagnitude = minMagnitude;
        this.depth = depth;
        this.northCoord = northCoord;
        this.westCoord = westCoord;
        this.predictionResult = predictionResult;
        this.createdAt = LocalDateTime.now();
    }

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Double getMinMagnitude() {
        return minMagnitude;
    }

    public void setMinMagnitude(Double minMagnitude) {
        this.minMagnitude = minMagnitude;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getNorthCoord() {
        return northCoord;
    }

    public void setNorthCoord(String northCoord) {
        this.northCoord = northCoord;
    }

    public String getWestCoord() {
        return westCoord;
    }

    public void setWestCoord(String westCoord) {
        this.westCoord = westCoord;
    }

    public String getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(String predictionResult) {
        this.predictionResult = predictionResult;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "HistoryEntry{"
               + "id=" + id +
               ", startDate='" + startDate + "'"
               + ", minMagnitude=" + minMagnitude +
               ", depth=" + depth +
               ", northCoord='" + northCoord + "'"
               + ", westCoord='" + westCoord + "'"
               + ", predictionResult='" + predictionResult + "'"
               + ", createdAt=" + createdAt +
               '}';
    }
}
