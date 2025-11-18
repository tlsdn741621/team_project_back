package com.busanit501.team_project.service;

import com.busanit501.team_project.dto.APIUserDTO;
import com.busanit501.team_project.dto.APIUserRegisterDTO;
import com.busanit501.team_project.dto.ChangePasswordRequestDTO;

public interface MemberService {
    void register(APIUserDTO apiUserDTO);
    boolean isRegistered(String memberId);
    void join(APIUserRegisterDTO apiUserRegisterDTO);
    boolean checkId(String memberId);
    APIUserDTO getMember(String memberId);
    void changePassword(String memberId, ChangePasswordRequestDTO requestDTO);

    APIUserDTO processSocialLogin(String registrationId, String socialId, String email, String nickname);
}
