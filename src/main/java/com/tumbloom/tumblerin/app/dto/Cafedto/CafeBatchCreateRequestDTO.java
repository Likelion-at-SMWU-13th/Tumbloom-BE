package com.tumbloom.tumblerin.app.dto.Cafedto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CafeBatchCreateRequestDTO {
    private List<CafeCreateRequestDTO> cafes; // 기존 DTO 리스트
}
