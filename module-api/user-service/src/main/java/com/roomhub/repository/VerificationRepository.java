package com.roomhub.repository;

import com.roomhub.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // 인증번호, 전송 시간 등 조회
    Optional<Verification> findVerificationByPhoneNumber(String phoneNumber);

    // 휴대폰 번호, 코드, 사용여부로 시간 조회
    Optional<Verification> findVerificationByPhoneNumberAndUsed(String phoneNumber, boolean used);
}
