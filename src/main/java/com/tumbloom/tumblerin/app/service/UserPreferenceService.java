package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Preference.ExtraOption;
import com.tumbloom.tumblerin.app.domain.Preference.PreferredMenu;
import com.tumbloom.tumblerin.app.domain.Preference.VisitPurpose;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.domain.UserPreference;
import com.tumbloom.tumblerin.app.dto.UserPreferenceDTO;
import com.tumbloom.tumblerin.app.repository.UserPreferenceRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    //저장(업데이트)
    public void saveOrUpdate(Long userId, UserPreferenceDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserPreference preference = userPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    UserPreference up = new UserPreference();
                    up.setUser(user);
                    return up;
                });

        preference.setVisitPurposes(convertToVisitPurposeEnum(dto.getVisitPurposes()));
        preference.setPreferredMenus(convertToPreferredMenuEnum(dto.getPreferredMenus()));
        preference.setExtraOptions(convertToExtraOptionEnum(dto.getExtraOptions()));

        userPreferenceRepository.save(preference);


    }

    //string을 enum 값으로 변환
    private List<VisitPurpose> convertToVisitPurposeEnum(List<String> list) {
        return list.stream()
                .map(VisitPurpose::valueOf)
                .collect(Collectors.toList());
    }

    private List<PreferredMenu> convertToPreferredMenuEnum(List<String> list) {
        return list.stream()
                .map(PreferredMenu::valueOf)
                .collect(Collectors.toList());
    }

    private List<ExtraOption> convertToExtraOptionEnum(List<String> list) {
        return list.stream()
                .map(ExtraOption::valueOf)
                .collect(Collectors.toList());
    }

    //enum 값을 string으로 변환
    private <T extends Enum<T>> List<String> convertEnumToStringList(List<T> enumList) {
        return enumList.stream()
                .map(Enum::name)  // Enum 이름 (예: "EMOTIONAL_ATMOSPHERE")을 String으로 변환
                .collect(Collectors.toList());
    }


}

