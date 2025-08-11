package com.tumbloom.tumblerin.app.domain;

import com.tumbloom.tumblerin.app.domain.Preference.ExtraOption;
import com.tumbloom.tumblerin.app.domain.Preference.PreferredMenu;
import com.tumbloom.tumblerin.app.domain.Preference.VisitPurpose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "user_preference")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<VisitPurpose> visitPurposes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<PreferredMenu> preferredMenus;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<ExtraOption> extraOptions;
}
