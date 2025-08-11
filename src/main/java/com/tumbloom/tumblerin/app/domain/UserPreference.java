package com.tumbloom.tumblerin.app.domain;

import com.tumbloom.tumblerin.app.domain.Preference.ExtraOption;
import com.tumbloom.tumblerin.app.domain.Preference.PreferredMenu;
import com.tumbloom.tumblerin.app.domain.Preference.VisitPurpose;
import jakarta.persistence.*;

import java.util.List;

@Table(name = "user_preference")
@Entity
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<VisitPurpose> visitPurposes;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PreferredMenu> preferredMenus;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ExtraOption> extraOptions;
}
