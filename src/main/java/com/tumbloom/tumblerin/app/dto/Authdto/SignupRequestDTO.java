package com.tumbloom.tumblerin.app.dto.Authdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDTO {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "회원 이메일", example = "newuser@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "securePassword123")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Schema(description = "사용자 닉네임", example = "coffee_lover")
    private String nickname;

}
