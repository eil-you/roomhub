package com.couchping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
@Getter
@Setter
public class Verification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 13, nullable = false)
    private String phoneNumber;
    @Column(length = 6, nullable = false)
    private String code;
    @Column(nullable = false)
    private LocalDateTime sendTime = LocalDateTime.now();
    @Column(nullable = false)
    private boolean used;
    @Column(nullable = false)
    private int failCount;

}
