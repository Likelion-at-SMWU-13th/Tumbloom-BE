package com.tumbloom.tumblerin.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cafeName;

    private String imageUrl;

    @Column(nullable = false)
    private String address;

    private String businessHours;

    @Column(columnDefinition = "POINT SRID 4326", nullable=false) // 위경도
    private Point location;

    @Column(nullable = false, unique = true)
    private String qrLink;

    @Column(nullable = false, unique = true)
    private String verificationCode;

    private String callNumber;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL)
    List<Menu> menuList =  new ArrayList<>();

    @Column(columnDefinition = "JSON")
    private String embedding;

    @Column(length = 1000)
    private String description; // 카페 설명

}
