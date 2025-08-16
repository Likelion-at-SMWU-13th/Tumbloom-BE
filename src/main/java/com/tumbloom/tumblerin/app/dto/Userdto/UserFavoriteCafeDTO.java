package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserFavoriteCafeDTO {
    private Long id;
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
}
