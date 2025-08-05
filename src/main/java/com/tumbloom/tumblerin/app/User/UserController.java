package com.tumbloom.tumblerin.app.User;

import com.tumbloom.tumblerin.app.User.dto.SignupRequestDTO;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ApiResponseTemplate.success(SuccessCode.USER_CREATED, "회원가입이 완료되었습니다.");
    }
}
