package com.roomhub.service.signup;

import com.roomhub.entity.Agreement;
import com.roomhub.entity.TermId;
import com.roomhub.entity.User;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ErrorCode;
import com.roomhub.model.SocialSignupRequest;
import com.roomhub.model.SubmitRequest;
import com.roomhub.model.Status;
import com.roomhub.repository.AgreementRepository;
import com.roomhub.repository.TermRepository;
import com.roomhub.repository.UserRepository;
import com.roomhub.repository.VerificationRepository;
import com.roomhub.util.AES256Util;
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

        // 전화번호 인증 1시간 이내, 닉네임 중복 체크
        isFormatValid(signupForm, phoneNumber);

        // 필수 약관 체크
        if (!areRequiredTermsChecked(signupForm.termIds()))
            throw new RoomHubException(ErrorCode.REQUIRED_TERMS_CHECKED);

        // db저장
        saveUserAndAgreement(signupForm, phoneNumber);
    }

    // 파라미터 NOT NULL, 유효성 검증
    public void isFormatValid(SubmitRequest signupForm, String phoneNumber) {

        // phoneNumber
        if (!isVerifiedPhoneNumber(phoneNumber))
            throw new RoomHubException(ErrorCode.PHONENUMBER_COOLDAWN);

        // email
        if (isEmailExist(signupForm.email()))
            throw new RoomHubException(ErrorCode.ALREADY_REGISTERED);

        // nickname
        if (isNicknameExist(signupForm.nickname()))
            throw new RoomHubException(ErrorCode.NICKNAME_IS_DUPLICATE);

    }

    // 닉네임 존재 여부 판단
    public boolean isNicknameExist(String nickname) {
        return userRepository.findByNickname(nickname) != null;
    }

    // 이메일 존재 여부 판단
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // 전화번호 복호화
    public String decryptPhoneNumber(String encryptedKey) {
        String phoneNumber;

        try {
            phoneNumber = AES256Util.decrypt(secretKey, encryptedKey);

        } catch (Exception e) {
            throw new RuntimeException("전화번호 복호화 실패", e);
        }
        return phoneNumber;
    }

    /*
     * 인증된 후 1시간 이내만 유효
     */
    public boolean isVerifiedPhoneNumber(String phoneNumber) {
        boolean used = true;

        return verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, used)
                .map(v -> {
                    return v.getSendTime().isAfter(LocalDateTime.now().minusHours(1));

                })
                .orElse(false);

    }

    /*
     * 필수 약관 체크
     */
    public boolean areRequiredTermsChecked(List<TermId> userTerms) {

        List<TermId> requiredsTerms = termRepository.findAllByRequiredAndActive(true, true);

        return userTerms != null && userTerms.containsAll(requiredsTerms);
    }

    /* 회원 가입 db 저장 및 agreement db 저장 */
    public void saveUserAndAgreement(SubmitRequest submitRequest, String phoneNumber) {
        User newUser = new User(submitRequest, phoneNumber);

        // 비밀 번호 암호화
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        User savedUser = userRepository.save(newUser);

        log.info("New user registered: {}", savedUser.getEmail());

        List<Agreement> agreements = Agreement.of(0, savedUser.getId(), submitRequest.termIds(), Status.AGREED);
        agreementRepository.saveAll(agreements);
    }

    @Transactional
    public void socialSubmit(String email, SocialSignupRequest socialSignupRequest) {
        String phoneNumber = decryptPhoneNumber(socialSignupRequest.encryptedKey());

        // 닉네임 중복 체크 등 검증
        if (isNicknameExist(socialSignupRequest.nickname())) {
            throw new RoomHubException(ErrorCode.NICKNAME_IS_DUPLICATE);
        }

        if (!isVerifiedPhoneNumber(phoneNumber)) {
            throw new RoomHubException(ErrorCode.PHONENUMBER_COOLDAWN);
        }

        if (!areRequiredTermsChecked(socialSignupRequest.termIds())) {
            throw new RoomHubException(ErrorCode.REQUIRED_TERMS_CHECKED);
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RoomHubException(ErrorCode.USER_NOT_FOUND);
        }

        user.updateSocialInfo(socialSignupRequest.birth(), socialSignupRequest.gender(), phoneNumber);
        user.setNickname(socialSignupRequest.nickname());
        userRepository.save(user);

        List<Agreement> agreements = Agreement.of(0, user.getId(), socialSignupRequest.termIds(), Status.AGREED);
        agreementRepository.saveAll(agreements);
    }
}
