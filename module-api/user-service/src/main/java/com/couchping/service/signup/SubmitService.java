package com.couchping.service.signup;

import com.couchping.entity.Agreement;
import com.couchping.entity.TermId;
import com.couchping.entity.User;
import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;
import com.couchping.model.SocialSignupRequest;
import com.couchping.model.SubmitRequest;
import com.couchping.model.Status;
import com.couchping.repository.AgreementRepository;
import com.couchping.repository.TermRepository;
import com.couchping.repository.UserRepository;
import com.couchping.repository.VerificationRepository;
import com.couchping.util.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitService {

    @Value("${aes.secret-key}")
    private String secretKey;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final TermRepository termRepository;
    private final AgreementRepository agreementRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void submit(SubmitRequest signupForm) {
        String phoneNumber = decryptPhoneNumber(signupForm.encryptedKey());

        // 1. 휴대폰 번호 복호화 및 유효성 검사
        isFormatValid(signupForm, phoneNumber);

        // 2. 필수 약관 동의 여부 확인
        if (!areRequiredTermsChecked(signupForm.termIds())) {
            throw new CouchPingException(UserErrorCode.REQUIRED_TERMS_CHECKED);
        }

        // 3. User 및 Agreement 저장
        saveUserAndAgreement(signupForm, phoneNumber);
    }

    // 휴대폰 번호, 이메일, 닉네임 유효성 검사
    public void isFormatValid(SubmitRequest signupForm, String phoneNumber) {
        // phoneNumber
        if (!isVerifiedPhoneNumber(phoneNumber)) {
            throw new CouchPingException(UserErrorCode.PHONENUMBER_COOLDOWN);
        }

        // email
        if (isEmailExist(signupForm.email())) {
            throw new CouchPingException(UserErrorCode.ALREADY_REGISTERED);
        }

        // nickname
        if (isNicknameExist(signupForm.nickname())) {
            throw new CouchPingException(UserErrorCode.NICKNAME_IS_DUPLICATE);
        }
    }

    // 닉네임 중복 확인
    public boolean isNicknameExist(String nickname) {
        return userRepository.findByNickname(nickname) != null;
    }

    // 이메일 중복 확인
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // 휴대폰 번호 복호화
    public String decryptPhoneNumber(String encryptedKey) {
        try {
            return AES256Util.decrypt(secretKey, encryptedKey);
        } catch (Exception e) {
            throw new CouchPingException(UserErrorCode.PHONENUMBER_FAILED_DECRYPT, e);
        }
    }

    /*
     * 휴대폰 인증 여부 확인 (1시간 이내)
     */
    public boolean isVerifiedPhoneNumber(String phoneNumber) {
        boolean used = true;

        return verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, used)
                .map(v -> v.getSendTime().isAfter(LocalDateTime.now().minusHours(1)))
                .orElse(false);
    }

    /*
     * 필수 약관 동의 여부 확인
     */
    public boolean areRequiredTermsChecked(List<TermId> userTerms) {
        List<TermId> requiredTerms = termRepository.findAllByRequiredAndActive(true, true);
        return userTerms != null && userTerms.containsAll(requiredTerms);
    }

    /* User 저장 및 Agreement 저장 */
    public void saveUserAndAgreement(SubmitRequest submitRequest, String phoneNumber) {
        User newUser = new User(submitRequest, phoneNumber);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        User savedUser = userRepository.save(newUser);

        log.info("New user registered: {}", savedUser.getEmail());

        List<Agreement> agreements = Agreement.of(savedUser.getId(), submitRequest.termIds(), Status.AGREED);
        agreementRepository.saveAll(agreements);
    }

    @Transactional
    public void socialSubmit(String email, SocialSignupRequest socialSignupRequest) {
        String phoneNumber = decryptPhoneNumber(socialSignupRequest.encryptedKey());

        // 닉네임 중복 체크
        if (isNicknameExist(socialSignupRequest.nickname())) {
            throw new CouchPingException(UserErrorCode.NICKNAME_IS_DUPLICATE);
        }

        if (!isVerifiedPhoneNumber(phoneNumber)) {
            throw new CouchPingException(UserErrorCode.PHONENUMBER_COOLDOWN);
        }

        if (!areRequiredTermsChecked(socialSignupRequest.termIds())) {
            throw new CouchPingException(UserErrorCode.REQUIRED_TERMS_CHECKED);
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CouchPingException(UserErrorCode.USER_NOT_FOUND);
        }

        user.updateSocialInfo(socialSignupRequest.birth(), socialSignupRequest.gender(), phoneNumber);
        user.setNickname(socialSignupRequest.nickname());
        userRepository.save(user);

        List<Agreement> agreements = Agreement.of(user.getId(), socialSignupRequest.termIds(), Status.AGREED);
        agreementRepository.saveAll(agreements);
    }
}
