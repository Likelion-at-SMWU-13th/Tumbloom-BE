package com.tumbloom.tumblerin.app.domain;

import com.tumbloom.tumblerin.app.domain.Preference.ExtraOption;
import com.tumbloom.tumblerin.app.domain.Preference.PreferredMenu;
import com.tumbloom.tumblerin.app.domain.Preference.VisitPurpose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<VisitPurpose> visitPurposes;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<PreferredMenu> preferredMenus;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<ExtraOption> extraOptions;
}
