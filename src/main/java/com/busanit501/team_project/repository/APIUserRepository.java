package com.busanit501.team_project.repository;

import com.busanit501.team_project.domain.APIUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface APIUserRepository extends JpaRepository<APIUser, String> {
    boolean existsByMemberId(String memberId);
    Optional<APIUser> findByMemberId(String memberId);
}
