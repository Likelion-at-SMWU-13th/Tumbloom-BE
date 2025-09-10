package com.tumbloom.tumblerin.app.dto.Authdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 응답 DTO")
public class TokenResponseDTO {

    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

}
