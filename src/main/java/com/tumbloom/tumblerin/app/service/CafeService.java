package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Menu;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeBatchCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeDetailResponseDTO;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final FavoriteRepository favoriteRepository;
    private final MenuRepository menuRepository;
    private final OpenAIEmbeddingService embeddingService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    //카페 정보 하나씩 등록하는 ver.
    public Cafe createCafe(CafeCreateRequestDTO request) {
        // latitude + longitude → Point
        Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        location.setSRID(4326);

        // description이 있으면 embedding 생성
        String embeddingJson = null;
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            embeddingJson = embeddingService.vectorToJson(
                    embeddingService.createEmbeddingList(request.getDescription())
            );
        }

        Cafe cafe = Cafe.builder()
                .cafeName(request.getCafeName())
                .imageUrl(request.getImageUrl())
                .address(request.getAddress())
                .businessHours(request.getBusinessHours())
                .location(location)
                .qrLink(request.getQrLink())
                .verificationCode(request.getVerificationCode())
                .callNumber(request.getCallNumber())
                .description(request.getDescription())
                .embedding(embeddingJson)
                .build();

        return cafeRepository.save(cafe);
    }

    //카페 정보 여러개를 한번에 등록하는 ver.
    @Transactional
    public List<Cafe> createCafeBatch(CafeBatchCreateRequestDTO batchRequest) {
        List<Cafe> cafeList = new ArrayList<>();

        for (CafeCreateRequestDTO request : batchRequest.getCafes()) {
            Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            location.setSRID(4326);

            String embeddingJson = null;
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                embeddingJson = embeddingService.vectorToJson(
                        embeddingService.createEmbeddingList(request.getDescription())
                );
            }

            Cafe cafe = Cafe.builder()
                    .cafeName(request.getCafeName())
                    .imageUrl(request.getImageUrl())
                    .address(request.getAddress())
                    .businessHours(request.getBusinessHours())
                    .location(location)
                    .qrLink(request.getQrLink())
                    .verificationCode(request.getVerificationCode())
                    .callNumber(request.getCallNumber())
                    .description(request.getDescription())
                    .embedding(embeddingJson)
                    .build();

            cafeList.add(cafe);
        }

        return cafeRepository.saveAll(cafeList);
    }

    // 카페 아이디로 카페 상세 정보 불러오기
    @Transactional(readOnly = true)
    public CafeDetailResponseDTO getCafeDetail(Long cafeId, Long userId) {

        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new IllegalArgumentException("카페를 찾을 수 없습니다. id=" + cafeId));

        boolean isFavorite = (userId != null) && favoriteRepository.existsByUserIdAndCafeId(userId, cafeId);

        List<Menu> menuList = menuRepository.findByCafeId(cafeId);
        List<CafeDetailResponseDTO.MenuDTO> menuDTOList = menuList.stream()
                .map(menu -> new CafeDetailResponseDTO.MenuDTO(
                        menu.getId(),
                        menu.getMenuName(),
                        menu.getPrice()
                ))
                .toList();

        return new CafeDetailResponseDTO(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getImageUrl(),
                cafe.getAddress(),
                cafe.getBusinessHours(),
                cafe.getCallNumber(),
                menuDTOList,
                isFavorite
        );
    }

}
