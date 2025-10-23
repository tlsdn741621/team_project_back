package com.busanit501.team_project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private Long Id;
    private String memberId;
    private String userName;
    private String email;
    private String password;

    public MemberDTO( String memberId, String userName, String email, String password) {
        this.memberId = memberId;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
