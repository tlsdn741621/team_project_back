package com.busanit501.team_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIUserRegisterDTO {
    private String memberId;
    private String password;
    private String userName;
    private String email;
}