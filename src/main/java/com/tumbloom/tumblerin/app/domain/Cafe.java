package com.tumbloom.tumblerin.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
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

    @Column(columnDefinition = "GEOMETRY", nullable=false) // 위경도
    private Point location;

    @Column(nullable = false, unique = true)
    private String qrLink;

    @Column(nullable = false, unique = true)
    private String verificationCode;

    private String callNumber;
}
