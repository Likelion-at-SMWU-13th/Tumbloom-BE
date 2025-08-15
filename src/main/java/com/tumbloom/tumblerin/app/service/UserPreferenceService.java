package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Preference.ExtraOption;
import com.tumbloom.tumblerin.app.domain.Preference.PreferredMenu;
import com.tumbloom.tumblerin.app.domain.Preference.VisitPurpose;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.domain.UserPreference;
import com.tumbloom.tumblerin.app.dto.Userdto.UserPreferenceDTO;
import com.tumbloom.tumblerin.app.repository.UserPreferenceRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
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

        preference.setVisitPurposes(convertToVisitPurposeEnum(new HashSet<>(dto.getVisitPurposes())));
        preference.setPreferredMenus(convertToPreferredMenuEnum(new HashSet<>(dto.getPreferredMenus())));
        preference.setExtraOptions(convertToExtraOptionEnum(new HashSet<>(dto.getExtraOptions())));

        userPreferenceRepository.save(preference);


    }

    //사용자 취향 조회
    public UserPreferenceDTO getPreference(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserPreference preference = userPreferenceRepository.findDetailByUserId(userId).orElse(null);

        if (preference == null) {
            // 아직 등록된 취향이 없으면 빈 리스트로 반환
            return UserPreferenceDTO.builder()
                    .visitPurposes(Collections.emptyList())
                    .preferredMenus(Collections.emptyList())
                    .extraOptions(Collections.emptyList())
                    .build();
        }

        return UserPreferenceDTO.builder()
                .visitPurposes(convertEnumToStringList(preference.getVisitPurposes()))
                .preferredMenus(convertEnumToStringList(preference.getPreferredMenus()))
                .extraOptions(convertEnumToStringList(preference.getExtraOptions()))
                .build();
    }





    //string을 enum 값으로 변환
    private Set<VisitPurpose> convertToVisitPurposeEnum(Set<String> list) {
        return list.stream()
                .map(VisitPurpose::valueOf)
                .collect(Collectors.toSet());
    }

    private Set<PreferredMenu> convertToPreferredMenuEnum(Set<String> list) {
        return list.stream()
                .map(PreferredMenu::valueOf)
                .collect(Collectors.toSet());
    }

    private Set<ExtraOption> convertToExtraOptionEnum(Set<String> list) {
        return list.stream()
                .map(ExtraOption::valueOf)
                .collect(Collectors.toSet());
    }

    //enum 값을 string으로 변환
    private <T extends Enum<T>> List<String> convertEnumToStringList(Collection<T> enumCollection) {
        return enumCollection.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }


}

