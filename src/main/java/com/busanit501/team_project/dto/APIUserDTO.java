package com.busanit501.team_project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class APIUserDTO extends User {
    private String memberId; // 사용자 ID
    private String password; // 사용자 비밀번호

    // 생성자
    public APIUserDTO(String memberId, String password, Collection<GrantedAuthority> authorities) {
        super(memberId, password, authorities); // 부모 클래스(User)의 생성자 호출
        this.memberId = memberId;
        this.password = password;
    }
}
