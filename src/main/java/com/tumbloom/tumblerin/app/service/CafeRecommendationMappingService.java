package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeListResponseDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeRecommendDTO;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CafeRecommendationMappingService {

    private final CafeRecommendationService cafeRecommendationService;
    private final CafeRepository cafeRepository;
    private final CafeService cafeService;

    public List<CafeListResponseDTO> getRecommendCafeList(Long userId) {

        List<CafeRecommendDTO> recommendCafeList = cafeRecommendationService.recommendCafesForUser(userId);
        if (recommendCafeList == null || recommendCafeList.isEmpty()) return List.of();

        return recommendCafeList.stream()
                .map(r -> toCafeListResponseDTO(r.getId(), r.isFavorite()))
                .toList();
    }

    public CafeListResponseDTO toCafeListResponseDTO(Long cafeId, boolean isFavorite) {

        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "카페를 찾을 수 없습니다."));

        return new CafeListResponseDTO(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getImageUrl(),
                cafe.getAddress(),
                cafe.getBusinessHours(),
                cafe.getLocation().getY(),
                cafe.getLocation().getX(),
                isFavorite
        );
    }

}

