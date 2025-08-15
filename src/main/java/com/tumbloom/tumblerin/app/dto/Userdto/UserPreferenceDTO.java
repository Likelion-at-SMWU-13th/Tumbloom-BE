package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceDTO {
    private List<String> visitPurposes;
    private List<String> preferredMenus;
    private List<String> extraOptions;
}
