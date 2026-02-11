package com.couchping.service.signup;

import com.couchping.entity.User;
import com.couchping.entity.Verification;
import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;
import com.couchping.model.VerifyRequest;
import com.couchping.model.VerifyResponse;
import com.couchping.repository.UserRepository;
import com.couchping.repository.VerificationRepository;
import com.couchping.util.AES256Util;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor()
public class VerificationCodeVerifyService {

    @Value("${aes.secret-key}")
    private String secretKey;
    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final VerificationCodePolicyService verificationCodePolicyService;

    @Transactional
    public VerifyResponse verifyCode(VerifyRequest verifyRequest) {

        String phoneNumber = verifyRequest.getPhoneNumber();
        String code = verifyRequest.getVerificationCode();

        Verification vc = verificationRepository.findVerificationByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CouchPingException(UserErrorCode.CODE_IS_EMPTY));

        if (!verificationCodePolicyService.isVerificationCodeValid(vc)) {
            throw new CouchPingException(UserErrorCode.CODE_EXPIRED);
        }

        if (!isCodeMatch(vc, code)) {
            incrementFailCount(vc);
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        if (!isAlreadyRegistered(phoneNumber)) {
            throw new CouchPingException(UserErrorCode.ALREADY_REGISTERED);
        }

        // ?紐꾩쵄 甕곕뜇??????db update)
        setVerificationUsed(vc);

        // ?袁れ넅甕곕뜇???酉???
        VerifyResponse verifyResponse = new VerifyResponse();
        try {
            verifyResponse.setEncryptedKey(AES256Util.encrypt(secretKey, phoneNumber));
        } catch (Exception e) {
            throw new CouchPingException(UserErrorCode.PHONENUMBER_FAILED_ENCRYPT);
        }

        return verifyResponse;
    }

    /* ?紐꾩쵄?꾨뗀諭??類ㅼ뵥 */
    public boolean isCodeMatch(Verification vc, String code) {
        return vc.getCode().equals(code);
    }

    /* ?袁れ넅甕곕뜇???紐꾩쵄 ?袁⑥┷ */
    @Transactional
    public void setVerificationUsed(Verification vc) {
        vc.setUsed(true);
        verificationRepository.save(vc);
    }

    /* ?紐꾩쵄?꾨뗀諭????귐됥늺 failCount+1 */
    public int incrementFailCount(Verification vc) {

        int failcount = vc.getFailCount();

        if (failcount == 3) {
            throw new CouchPingException(UserErrorCode.CODE_FAILD_COUNT_LIMIT);
        }

        vc.setFailCount(failcount + 1);

        verificationRepository.save(vc);
        return failcount;
    }

    /* ?袁れ넅甕곕뜇?뉐첎? ??? 揶쎛??낅쭆 ?④쑴??紐? 筌ｋ똾寃?*/
    public boolean isAlreadyRegistered(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);

        if (user != null) {
            return false;
        }
        return true;
    }

}

