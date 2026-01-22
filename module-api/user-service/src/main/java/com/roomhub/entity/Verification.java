package com.roomhub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
@Getter
@Setter
public class Verification extends baseTimeEntity {
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
