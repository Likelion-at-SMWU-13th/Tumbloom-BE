package com.tumbloom.tumblerin.app.dto.Cafedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
public class CafeListResponseDTO {

    private Long id;
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
    private double latitude;
    private double longitude;

}
