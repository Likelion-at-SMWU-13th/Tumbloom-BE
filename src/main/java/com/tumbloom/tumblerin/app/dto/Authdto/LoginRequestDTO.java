package com.tumbloom.tumblerin.app.dto.Authdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;
    @Schema(description = "비밀번호", example = "1234")
    private String password;
}
