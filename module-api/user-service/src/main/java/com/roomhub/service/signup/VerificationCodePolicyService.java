package com.roomhub.service.signup;

import com.roomhub.entity.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor()
public class VerificationCodePolicyService {

    // 인증번호 재발송 가능 여부 검사
    public boolean isResendableAllowd(Verification vc) {
        return vc.getSendTime().isBefore(LocalDateTime.now().minusMinutes(3));
    }

    // 인증번호 (시간 검사)
    public boolean isVerificationCodeValid(Verification vc) {

        return vc.getSendTime().isAfter(LocalDateTime.now().minusMinutes(3));
    }

}
