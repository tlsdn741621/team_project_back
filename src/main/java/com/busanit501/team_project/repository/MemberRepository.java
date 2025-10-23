package com.busanit501.team_project.repository;

import com.busanit501.team_project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByMemberId(String memberId);
    Optional<Member> findByMemberId(String memberId);
}
