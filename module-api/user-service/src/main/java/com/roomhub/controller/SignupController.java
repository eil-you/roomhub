package com.roomhub.controller;

import com.roomhub.model.*;
import com.roomhub.model.SocialSignupRequest;
import com.roomhub.service.signup.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor()
public class SignupController {

    private final SendVerificationCodeService sendVerificationCodeService;
    private final VerificationCodeVerifyService verificationCodeVerifyService;
    private final NicknameService nicknameService;
    private final SubmitService submitService;
    private final ShowTermsService showTermsService;

    /* 약관 동의 LIST 가져오기 */
    @GetMapping("/terms")
    public CommonResponse<List<TermsResponse>> showTerms() {
        List<TermsResponse> termsResponse = showTermsService.getTerms();
        return new CommonResponse(0, null, termsResponse);
    }

    /* 인증번호 생성 (SMS 발송) */
    @PostMapping("/verifications/sms")
    public void send(@RequestParam String phoneNumber) {
        sendVerificationCodeService.sendVerificationCode(phoneNumber);
    }

    /* 인증번호 검증 */
    @PostMapping("/verifications/sms/confirm")
    public CommonResponse<VerifyResponse> verify(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse verifyResponse = verificationCodeVerifyService.verifyCode(verifyRequest);
        return new CommonResponse(0, null, verifyResponse);
    }

    /* 랜덤 닉네임 생성 */
    @GetMapping("/nicknames/random")
    public CommonResponse<NicknameResponse> randomNickname() {
        NicknameResponse nicknameResponse = nicknameService.generateNickname();
        return new CommonResponse(0, null, nicknameResponse);
    }

    /* 이메일 중복 체크 */
    @GetMapping("/users/check-email")
    public CommonResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicate = submitService.isEmailExist(email);
        return new CommonResponse<>(0, null, isDuplicate);
    }

    /* 닉네임 중복 체크 */
    @GetMapping("/users/check-nickname")
    public CommonResponse<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = submitService.isNicknameExist(nickname);
        return new CommonResponse<>(0, null, isDuplicate);
    }

    /* 회원가입 (유저 생성) */
    @PostMapping("/users")
    public void submit(@Valid @RequestBody SubmitRequest submitRequest) {
        submitRequest.check();
        submitService.submit(submitRequest);
    }

    /* 소셜 회원가입 (추가 정보 입력) */
    @PostMapping("/users/social")
    public void socialSubmit(@Valid @RequestBody SocialSignupRequest socialSignupRequest,
            org.springframework.security.core.Authentication authentication) {
        socialSignupRequest.check();
        String email = authentication.getName();
        submitService.socialSubmit(email, socialSignupRequest);
    }

}
