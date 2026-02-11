package com.couchping.service.signup;

import com.couchping.entity.Verification;
import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;
import com.couchping.repository.VerificationRepository;
import com.couchping.util.RedisUtil;
import com.couchping.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor()
public class SendVerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(SendVerificationCodeService.class);
    private final VerificationRepository verificationRepository;
    private final VerificationCodePolicyService verificationCodePolicyService;
    private final RedisUtil redisUtil;

    public void sendVerificationCode(String phoneNumber) {

        ValidationUtil.checkPhoneNumber(phoneNumber);

        // Rate limiting: 1분 이내 재발송 방지
        if (redisUtil.getData("SMS_LIMIT:" + phoneNumber) != null) {
            throw new CouchPingException(UserErrorCode.PHONENUMBER_COOLDOWN);
        }

        Optional<Verification> optionalVC = verificationRepository.findVerificationByPhoneNumber(phoneNumber);

        if (optionalVC.isPresent()) {
            if (!verificationCodePolicyService.isResendableAllowd(optionalVC.get())) {
                throw new CouchPingException(UserErrorCode.RESEND_COOLDOWN);
            }
        }

        String code = makeRandomCode();
        saveVerificationCode(optionalVC, phoneNumber, code);

        // SMS 발송 제한 설정 (1분)
        redisUtil.setData("SMS_LIMIT:" + phoneNumber, "true", 60000L);
    }

    /*
     * 랜덤 인증번호 생성
     */
    public String makeRandomCode() {
        // 1000 ~ 9999
        String randomCode = String.valueOf((int) (Math.random() * 9000) + 1000);

        log.info("생성된 인증번호: {}", randomCode);
        return randomCode;
    }

    /*
     * 인증번호 DB 저장
     */
    public void saveVerificationCode(Optional<Verification> optionalVC, String phoneNumber, String code) {

        Verification vc;

        if (optionalVC.isPresent()) {
            vc = optionalVC.get();
            vc.setCode(code);
            vc.setSendTime(LocalDateTime.now());
            vc.setFailCount(0);

        } else {
            vc = new Verification();
            vc.setPhoneNumber(phoneNumber);
            vc.setCode(code);
        }

        verificationRepository.save(vc);
    }

}
