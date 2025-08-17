package com.tumbloom.tumblerin.app.dto.Authdto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDTO {

    private String accessToken;
    private String refreshToken;

}
