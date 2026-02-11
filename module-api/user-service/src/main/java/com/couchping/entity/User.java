package com.couchping.entity;

import com.couchping.model.SubmitRequest;
import jakarta.persistence.*;
import com.couchping.model.Gender;
import com.couchping.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.couchping.model.Language;
import com.couchping.model.Interest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255) // OAuth2 ?ъ슜?먮뒗 鍮꾨?踰덊샇媛 ?놁쓣 ?섎룄 ?덉쓬 (nullable)
    private String password;

    @Column(nullable = true)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider; // google
    private String providerId; // sub returned from provider

    @Column(columnDefinition = "TEXT")
    private String bio; // ?먭린?뚭컻

    @Column(columnDefinition = "TEXT")
    private String lifestyle; // ?쇱씠?꾩뒪????깊뼢

    private String profileImage; // ?꾨줈???대?吏 URL

    @Column(nullable = false)
    private double trustScore = 50; // ?좊ː ?먯닔 (湲곕낯媛?50??

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private double hostRating = 0.0; // ?몄뒪???됱젏

    @Column(nullable = false)
    private int hostReviewCount = 0; // ?몄뒪??由щ럭 媛쒖닔

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private double guestRating = 0.0; // 寃뚯뒪???됱젏

    @Column(nullable = false)
    private int guestReviewCount = 0; // 寃뚯뒪??由щ럭 媛쒖닔

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Set<Language> languages = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "interest")
    private Set<Interest> interests = new HashSet<>();

    // Email???앹꽦??
    public User(SubmitRequest dto, String phoneNumber) {
        this.email = dto.email();
        this.password = dto.password();
        this.birth = dto.birth();
        this.gender = Gender.valueOf(dto.gender().toString());
        this.nickname = dto.nickname();
        this.phoneNumber = phoneNumber;
        this.role = Role.USER;
    }

    // OAuth2???앹꽦??
    public User(String email, String nickname, Role role, String provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.birth = null;
        this.gender = null;
        this.phoneNumber = null;
        this.password = null;
    }

    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public void updateSocialInfo(LocalDate birth, Gender gender, String phoneNumber) {
        this.birth = birth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.role = Role.USER;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
