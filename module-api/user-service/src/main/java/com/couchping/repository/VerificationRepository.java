package com.couchping.repository;

import com.couchping.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // ?몄쬆踰덊샇, ?꾩넚 ?쒓컙 ??議고쉶
    Optional<Verification> findVerificationByPhoneNumber(String phoneNumber);

    // ?대???踰덊샇, 肄붾뱶, ?ъ슜?щ?濡??쒓컙 議고쉶
    Optional<Verification> findVerificationByPhoneNumberAndUsed(String phoneNumber, boolean used);
}
