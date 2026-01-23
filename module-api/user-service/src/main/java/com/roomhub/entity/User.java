package com.roomhub.entity;

import com.roomhub.model.SubmitRequest;
import jakarta.persistence.*;
import com.roomhub.model.Gender;
import com.roomhub.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(length = 255) // OAuth2 사용자는 비밀번호가 없을 수도 있음 (nullable)
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

    private String provider; // google, kakao, naver
    private String providerId; // sub returned from provider

    // Email용 생성자
    public User(SubmitRequest dto, String phoneNumber) {
        this.email = dto.email();
        this.password = dto.password();
        this.birth = dto.birth();
        this.gender = Gender.valueOf(dto.gender().toString());
        this.nickname = dto.nickname();
        this.phoneNumber = phoneNumber;
        this.role = Role.USER;
    }

    // OAuth2용 생성자
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
