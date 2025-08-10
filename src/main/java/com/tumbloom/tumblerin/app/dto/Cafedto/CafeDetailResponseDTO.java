package com.tumbloom.tumblerin.app.dto.Cafedto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CafeDetailResponseDTO {

    private Long id;
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
    private String callNumber;
    private List<MenuDTO> menuList;

    @Getter
    @AllArgsConstructor
    public static class MenuDTO {
        private Long id;
        private String menuName;
        private Integer price;
    }

}
