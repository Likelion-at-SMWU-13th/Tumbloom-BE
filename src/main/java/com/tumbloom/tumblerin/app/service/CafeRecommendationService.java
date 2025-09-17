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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CafeRecommendationService {

    private final CafeRepository cafeRepository;
    private final UserPreferenceRepository upRepository;
    private final OpenAIEmbeddingService embeddingService;
    private final FavoriteRepository favoriteRepository;

    //캐시 맵 적용
    private final Map<Long, float[]> cafeEmbeddingCache = new ConcurrentHashMap<>();

    // 최초 혹은 변경 시 캐시 초기화
    @PostConstruct
    public void initCache() {
        List<Cafe> cafes = cafeRepository.findAll();
        for (Cafe cafe : cafes) {
            if (cafe.getEmbedding() != null) {
                List<Double> vecList = embeddingService.jsonToVector(cafe.getEmbedding());
                float[] vector = new float[vecList.size()];
                for (int i = 0; i < vecList.size(); i++) vector[i] = vecList.get(i).floatValue();
                cafeEmbeddingCache.put(cafe.getId(), vector);
            }
        }
    }

    public List<CafeRecommendDTO> recommendCafesForUser(Long userId) {

        UserPreference preference = upRepository.findDetailByUserId(userId)
                .orElse(null);

        if (preference == null || (preference.getVisitPurposes().isEmpty()
                && preference.getPreferredMenus().isEmpty()
                && preference.getExtraOptions().isEmpty())) {
            return Collections.emptyList();
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
                    float[] cafeEmbedding = cafeEmbeddingCache.get(cafe.getId());
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

    private double cosineSimilarity(List<Double> v1, float[] v2) {
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v2.length; i++) {
            double val1 = v1.get(i);
            dot += val1 * v2[i];
            norm1 += val1 * val1;
            norm2 += v2[i] * v2[i];
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private <T extends Enum<T>> List<String> convertEnumToStringList(Collection<T> enumCollection) {
        return enumCollection.stream().map(Enum::name).collect(Collectors.toList());
    }

    // 카페 생성 시 캐시 갱신
    public void updateCacheForNewCafe(Cafe cafe) {
        if (cafe.getEmbedding() != null) {
            List<Double> vecList = embeddingService.jsonToVector(cafe.getEmbedding());
            float[] vector = new float[vecList.size()];
            for (int i = 0; i < vecList.size(); i++) vector[i] = vecList.get(i).floatValue();
            cafeEmbeddingCache.put(cafe.getId(), vector);
        }
    }


}
