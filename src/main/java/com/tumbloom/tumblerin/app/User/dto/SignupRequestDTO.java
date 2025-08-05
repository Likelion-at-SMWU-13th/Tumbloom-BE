package com.tumbloom.tumblerin.app.User.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    private String email;
    private String password;
    private String nickname;

}
