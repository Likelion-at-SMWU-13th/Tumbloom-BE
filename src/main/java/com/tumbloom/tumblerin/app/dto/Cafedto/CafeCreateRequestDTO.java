package com.tumbloom.tumblerin.app.dto.Cafedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<MenuCreateRequestDTO> menuList;

    @Getter
    @Setter
    public static class MenuCreateRequestDTO {
        private String menuName;
        private Integer price;
    }
}