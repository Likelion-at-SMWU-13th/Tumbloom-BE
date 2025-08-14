package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.domain.UserPreference;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeRecommendDTO;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.repository.UserPreferenceRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CafeRecommendationService {

    private final CafeRepository cafeRepository;
    private final UserPreferenceRepository upRepository;
    private final OpenAIEmbeddingService embeddingService;
    private final FavoriteRepository favoriteRepository;

    public List<CafeRecommendDTO> recommendCafesForUser(Long userId) {

        UserPreference preference = upRepository.findDetailByUserId(userId)
                .orElse(null);

        if (preference == null) {
            return null;
        }

        // 1. 사용자 취향 → 텍스트 변환
        String userText = Stream.of(
                        List.of("방문목적: " + String.join(", ", convertEnumToStringList(preference.getVisitPurposes()))),
                        List.of("선호메뉴: " + String.join(", ", convertEnumToStringList(preference.getPreferredMenus()))),
                        List.of("기타옵션: " + String.join(", ", convertEnumToStringList(preference.getExtraOptions())))
                )
                .flatMap(List::stream)
                .collect(Collectors.joining("; "));

        // 2. 사용자 텍스트 임베딩
        List<Double> userEmbedding = embeddingService.createEmbeddingList(userText);

        // 3. DB에 있는 카페 모두 조회
        List<Cafe> cafes = cafeRepository.findAll();

        // 3-1. 사용자 즐겨찾기 카페 ID 목록
        List<Long> favoriteCafeIds = favoriteRepository.findCafeIdsByUserId(userId);

        // 4. 코사인 유사도 계산
        return cafes.stream()
                .map(cafe -> {
                    List<Double> cafeEmbedding = embeddingService.jsonToVector(cafe.getEmbedding());
                    double similarity = cosineSimilarity(userEmbedding, cafeEmbedding);
                    boolean isFavorite = favoriteCafeIds.contains(cafe.getId());

                    return new CafeRecommendDTO(cafe, similarity, isFavorite); //
                })
                .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
                .limit(5) // 상위 5개만 추천
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private <T extends Enum<T>> List<String> convertEnumToStringList(Collection<T> enumCollection) {
        return enumCollection.stream().map(Enum::name).collect(Collectors.toList());
    }

}
