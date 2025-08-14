package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@RequiredArgsConstructor
public class CafePointRepositoryImpl implements CafePointRepository {

    private final EntityManager em;

    // 3km 이내 모든 카페
    @Override
    public Slice<Cafe> findNearbyCafes(double lon, double lat, double radiusMeters, Pageable pageable) {

        int pageSize = pageable.getPageSize();

        String sliceSQL = """
                SELECT *
                FROM cafe c
                WHERE ST_Distance_Sphere(
                        c.location, ST_SRID(POINT(:lon, :lat), 4326)
                      ) <= :radius
                ORDER BY
                    ST_Distance_Sphere(c.location, ST_SRID(POINT(:lon, :lat), 4326)) ASC,
                    c.id ASC
                LIMIT :limit OFFSET :offset
                    """;

        @SuppressWarnings("unchecked")
        List<Cafe> sliceResult = em.createNativeQuery(sliceSQL, Cafe.class)
                .setParameter("lon", lon)
                .setParameter("lat", lat)
                .setParameter("radius", radiusMeters)
                .setParameter("limit", pageSize+1)
                .setParameter("offset", (int)pageable.getOffset())
                .getResultList();

        boolean hasNext = sliceResult.size() > pageSize;
        if (hasNext) {
            sliceResult = sliceResult.subList(0, pageSize);
        }

        return new SliceImpl<>(sliceResult,  pageable, hasNext);
    }

    // 3km 이내 카페들 중 top5
    @Override
    public List<Cafe> findTopNearbyCafes(double lon, double lat, double radiusMeters, int limit) {

        String listSQL = """
               SELECT *
               FROM cafe c
               WHERE ST_Distance_Sphere(
                       c.location, ST_SRID(POINT(:lon, :lat), 4326)
                     ) <= :radius
               ORDER BY
                   ST_Distance_Sphere(c.location, ST_SRID(POINT(:lon, :lat), 4326)) ASC,
                   c.id ASC
               LIMIT :limit
               """;

        @SuppressWarnings("unchecked")
        List<Cafe> listResult = em.createNativeQuery(listSQL, Cafe.class)
                .setParameter("lon", lon)                 // 경도(X)
                .setParameter("lat", lat)                 // 위도(Y)
                .setParameter("radius", radiusMeters)     // 예: 3000
                .setParameter("limit", limit)             // 예: 5
                .getResultList();

        return listResult;

    }
}
