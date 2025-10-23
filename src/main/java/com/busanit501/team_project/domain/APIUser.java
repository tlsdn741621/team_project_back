package com.busanit501.team_project.domain;

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

    private String password;

    public void changePw(String mpw) {
        this.password = mpw;
    }

}
