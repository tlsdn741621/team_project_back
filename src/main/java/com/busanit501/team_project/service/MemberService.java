package com.busanit501.team_project.service;

import com.busanit501.team_project.domain.Member;
import com.busanit501.team_project.dto.ChangePasswordRequestDTO;
import com.busanit501.team_project.dto.MemberDTO;

public interface MemberService {
    void register(Member member);
    boolean isRegistered(String memberId);
    void join(MemberDTO memberDTO);
    boolean checkId(String memberId);
    MemberDTO getMember(String memberId);
    void changePassword(String memberId, ChangePasswordRequestDTO requestDTO);
}

