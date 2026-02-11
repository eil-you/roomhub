package com.couchping.service.signup;

import com.couchping.entity.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor()
public class VerificationCodePolicyService {

    // ?몄쬆踰덊샇 ?щ컻??媛???щ? 寃??
    public boolean isResendableAllowd(Verification vc) {
        return vc.getSendTime().isBefore(LocalDateTime.now().minusMinutes(3));
    }

    // ?몄쬆踰덊샇 (?쒓컙 寃??
    public boolean isVerificationCodeValid(Verification vc) {

        return vc.getSendTime().isAfter(LocalDateTime.now().minusMinutes(3));
    }

}
