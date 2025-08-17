package com.tumbloom.tumblerin.app.dto.Authdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequestDTO {
    private String refreshToken;
}
