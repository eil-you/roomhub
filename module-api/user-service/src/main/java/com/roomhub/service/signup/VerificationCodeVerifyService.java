package com.roomhub.service.signup;

import com.roomhub.entity.User;
import com.roomhub.entity.Verification;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ErrorCode;
import com.roomhub.model.VerifyRequest;
import com.roomhub.model.VerifyResponse;
import com.roomhub.repository.UserRepository;
import com.roomhub.repository.VerificationRepository;
import com.roomhub.util.AES256Util;

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
                .orElseThrow(() -> new RoomHubException(ErrorCode.CODE_IS_EMPTY));

        if (!verificationCodePolicyService.isVerificationCodeValid(vc)) {
            throw new RoomHubException(ErrorCode.CODE_EXPIRED);
        }

        if (!isCodeMatch(vc, code)) {
            incrementFailCount(vc);
            throw new RoomHubException(ErrorCode.CODE_NOT_VALID);
        }

        if (!isAlreadyRegistered(phoneNumber)) {
            throw new RoomHubException(ErrorCode.ALREADY_REGISTERED);
        }

        // 인증 번호 사용(db update)
        setVerificationUsed(vc);

        // 전화번호 암호화
        VerifyResponse verifyResponse = new VerifyResponse();
        try {
            verifyResponse.setEncryptedKey(AES256Util.encrypt(secretKey, phoneNumber));
        } catch (Exception e) {
            throw new RoomHubException(ErrorCode.PHONENUMBER_FAILED_ENCRYPT);
        }

        return verifyResponse;
    }

    /* 인증코드 확인 */
    public boolean isCodeMatch(Verification vc, String code) {
        return vc.getCode().equals(code);
    }

    /* 전화번호 인증 완료 */
    @Transactional
    public void setVerificationUsed(Verification vc) {
        vc.setUsed(true);
        verificationRepository.save(vc);
    }

    /* 인증코드 틀리면 failCount+1 */
    public int incrementFailCount(Verification vc) {

        int failcount = vc.getFailCount();

        if (failcount == 3) {
            throw new RoomHubException(ErrorCode.CODE_FAILD_COUNT_LIMIT);
        }

        vc.setFailCount(failcount + 1);

        verificationRepository.save(vc);
        return failcount;
    }

    /* 전화번호가 이미 가입된 계정인지 체크 */
    public boolean isAlreadyRegistered(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);

        if (user != null) {
            return false;
        }
        return true;
    }

}
