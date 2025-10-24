package com.busanit501.team_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MemberDTO {
    private String memberId;
    private String userName;
    private String email;
    private String password;
    private boolean social;

    public MemberDTO( String memberId, String userName, String email, String password) {
        this.memberId = memberId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.social = false; // 이 생성자로 호출되면 social은 false
    }

}
