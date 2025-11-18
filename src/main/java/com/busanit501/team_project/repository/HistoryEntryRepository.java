package com.busanit501.team_project.repository;

import com.busanit501.team_project.domain.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryEntryRepository extends JpaRepository<HistoryEntry, Long> {
    // 사용자 ID로 기록을 조회하는 메서드 (인증 구현 시 사용)
    List<HistoryEntry> findByMemberIdOrderByCreatedAtDesc(String memberId);

    // 모든 기록을 최신순으로 조회하는 메서드 (현재는 사용자 ID 없이)
    List<HistoryEntry> findAllByOrderByCreatedAtDesc();
}
