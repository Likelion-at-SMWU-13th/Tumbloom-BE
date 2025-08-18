package com.tumbloom.tumblerin.app.dto.Cafedto;

import com.tumbloom.tumblerin.app.domain.Cafe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CafeRecommendDTO {

    private Long id;
    private String cafeName;
    private String address;
    private String imageUrl;
    private double similarity;
    private boolean favorite;


    public CafeRecommendDTO(Cafe cafe, double similarity, boolean favorite) {
        this.id = cafe.getId();
        this.cafeName = cafe.getCafeName();
        this.address = cafe.getAddress();
        this.imageUrl = cafe.getImageUrl();
        this.similarity = similarity;
        this.favorite = favorite;
    }
}
