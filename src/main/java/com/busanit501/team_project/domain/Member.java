package com.busanit501.team_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    @Id
    private String memberId;

    @Column(nullable = false)
    private String password;

    @Column(name = "userName")
    private String userName;

    @Column(name = "email")
    private String email;

    private String role;
    private boolean social;

    public void changePw(String mpw) {
        this.password = mpw;
    }
}
