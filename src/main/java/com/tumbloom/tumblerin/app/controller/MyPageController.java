package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.UserPreferenceDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.UserPreferenceService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserPreferenceService userPreferenceService;

    @PostMapping("/preference")
    public ResponseEntity<?> savePreference(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPreferenceDTO dto) {

        userPreferenceService.saveOrUpdate(userDetails.getUser().getId(),dto);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED,null);
    }

}
