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
public class APIUser {

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

    public void changeUserName(String userName) {
        this.userName = userName;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeRole(String role) {
        this.role = role;
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }
}
