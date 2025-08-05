package com.tumbloom.tumblerin.app.User;

import com.tumbloom.tumblerin.app.User.dto.*;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ApiResponseTemplate.success(SuccessCode.USER_CREATED, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response= userService.login(request);
        return ApiResponseTemplate.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ApiResponseTemplate.success(SuccessCode.LOGOUT_SUCCESSFUL, null);
    }
}
