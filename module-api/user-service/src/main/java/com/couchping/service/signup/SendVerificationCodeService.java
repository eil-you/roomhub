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

        // Rate limiting: 1????沅???덉뵬 甕곕뜇??獄쏆뮇????쀫립
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

        // 獄쏆뮇????쀫립 ????쇱젟 (1??
        redisUtil.setData("SMS_LIMIT:" + phoneNumber, "true", 60000L);
    }

    /*
     * ??뺣쑁 ?꾨뗀諭???밴쉐
     */
    public String makeRandomCode() {
        // 1000 ~ 9999
        String randomCode = String.valueOf((int) (Math.random() * 9000) + 1000);

        log.info("??밴쉐???紐꾩쵄甕곕뜇??: {}", randomCode);
        return randomCode;
    }

    /*
     * ?紐꾩쵄甕곕뜇??db??????
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

