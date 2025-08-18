package com.tumbloom.tumblerin.app.dto.Userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceDTO {
    @ArraySchema(
            arraySchema = @Schema(description = "방문 목적 (부분일치 가능)"),
            schema = @Schema(implementation = String.class, allowableValues = {
                    "EMOTIONAL_ATMOSPHERE", "STUDY_WORKSPACE", "CHAT_MEETING", "HOT_PLACE", "EVENT"
            })
    )
    private List<String> visitPurposes;
    @ArraySchema(
            arraySchema = @Schema(description = "선호 메뉴"),
            schema = @Schema(implementation = String.class, allowableValues = {
                    "SPECIALTY", "DESSERT", "DECAF", "SEASON_MENU", "BRUNCH"
            })
    )
    private List<String> preferredMenus;
    @ArraySchema(
            arraySchema = @Schema(description = "추가 옵션"),
            schema = @Schema(implementation = String.class, allowableValues = {
                    "FRANCHISE", "PET_FRIENDLY", "OUTDOOR_TERRACE", "ECO_LOCAL", "UNIQUE_THEME"
            })
    )
    private List<String> extraOptions;
}
