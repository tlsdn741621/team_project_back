package com.busanit501.team_project.controller;

import com.busanit501.team_project.domain.HistoryEntry;
import com.busanit501.team_project.repository.HistoryEntryRepository;
import com.busanit501.team_project.dto.APIUserDTO; // APIUserDTO import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryEntryRepository historyEntryRepository;

    @Autowired
    public HistoryController(HistoryEntryRepository historyEntryRepository) {
        this.historyEntryRepository = historyEntryRepository;
    }

    /**
     * 새로운 기록 항목을 저장합니다.
     * POST /api/history
     * @param historyEntry 저장할 기록 항목 데이터
     * @return 저장된 기록 항목과 HTTP 201 Created 상태
     */
    @PostMapping
    public ResponseEntity<HistoryEntry> createHistoryEntry( @RequestBody HistoryEntry historyEntry) {
        String currentMemberId = getCurrentMemberId();
        if (currentMemberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 사용자
        }
        historyEntry.setMemberId(currentMemberId);

        HistoryEntry savedEntry = historyEntryRepository.save(historyEntry);
        return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
    }

    /**
     * 현재 사용자의 모든 기록 항목을 최신순으로 조회합니다.
     * GET /api/history
     * @return 기록 항목 리스트와 HTTP 200 OK 상태
     */
    @GetMapping
    public ResponseEntity<List<HistoryEntry>> getAllHistoryEntries() {
        String currentMemberId = getCurrentMemberId();
        if (currentMemberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 사용자
        }

        List<HistoryEntry> entries = historyEntryRepository.findByMemberIdOrderByCreatedAtDesc(currentMemberId);
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    /**
     * Spring Security 컨텍스트에서 현재 인증된 사용자의 memberId를 가져옵니다.
     * @return 현재 사용자의 memberId 또는 인증되지 않은 경우 null
     */
    private String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof APIUserDTO) {
            APIUserDTO apiUserDTO = (APIUserDTO) authentication.getPrincipal();
            return apiUserDTO.getMemberId();
        }
        return null;
    }
}
