package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeBatchCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeCreateRequestDTO;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
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
}
