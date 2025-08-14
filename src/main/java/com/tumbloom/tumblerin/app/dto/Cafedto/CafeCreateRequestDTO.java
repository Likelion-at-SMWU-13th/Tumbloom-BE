package com.tumbloom.tumblerin.app.dto.Cafedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CafeCreateRequestDTO {
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
    private double latitude;
    private double longitude;
    private String qrLink;
    private String verificationCode;
    private String callNumber;
    private String description;
}