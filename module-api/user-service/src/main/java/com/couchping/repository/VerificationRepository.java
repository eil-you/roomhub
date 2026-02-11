package com.couchping.repository;

import com.couchping.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // 휴대폰 번호로 인증 정보 조회
    Optional<Verification> findVerificationByPhoneNumber(String phoneNumber);

    // 휴대폰 번호와 사용 여부로 인증 정보 조회
    Optional<Verification> findVerificationByPhoneNumberAndUsed(String phoneNumber, boolean used);
}
