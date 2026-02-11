package com.couchping.controller;

import com.couchping.model.*;
import com.couchping.service.signup.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SignupController {

    private static final int SUCCESS_CODE = 0;

    private final SendVerificationCodeService sendVerificationCodeService;
    private final VerificationCodeVerifyService verificationCodeVerifyService;
    private final NicknameService nicknameService;
    private final SubmitService submitService;
    private final ShowTermsService showTermsService;

    /* ?쎄? ?숈쓽 LIST 媛?몄삤湲?*/
    @GetMapping("/terms")
    public CommonResponse<List<TermsResponse>> showTerms() {
        List<TermsResponse> termsResponse = showTermsService.getTerms();
        return new CommonResponse<>(SUCCESS_CODE, null, termsResponse);
    }

    /* ?몄쬆踰덊샇 ?앹꽦 (SMS 諛쒖넚) */
    @PostMapping("/verifications/sms")
    public CommonResponse<Void> send(@RequestParam String phoneNumber) {
        sendVerificationCodeService.sendVerificationCode(phoneNumber);
        return new CommonResponse<>(SUCCESS_CODE, "Verification code sent", null);
    }

    /* ?몄쬆踰덊샇 寃利?*/
    @PostMapping("/verifications/sms/confirm")
    public CommonResponse<VerifyResponse> verify(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse verifyResponse = verificationCodeVerifyService.verifyCode(verifyRequest);
        return new CommonResponse<>(SUCCESS_CODE, null, verifyResponse);
    }

    /* ?쒕뜡 ?됰꽕???앹꽦 */
    @GetMapping("/nicknames/random")
    public CommonResponse<NicknameResponse> randomNickname() {
        NicknameResponse nicknameResponse = nicknameService.generateNickname();
        return new CommonResponse<>(SUCCESS_CODE, null, nicknameResponse);
    }

    /* ?대찓??以묐났 泥댄겕 */
    @GetMapping("/users/check-email")
    public CommonResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicate = submitService.isEmailExist(email);
        return new CommonResponse<>(SUCCESS_CODE, null, isDuplicate);
    }

    /* ?됰꽕??以묐났 泥댄겕 */
    @GetMapping("/users/check-nickname")
    public CommonResponse<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = submitService.isNicknameExist(nickname);
        return new CommonResponse<>(SUCCESS_CODE, null, isDuplicate);
    }

    /* ?뚯썝媛??(?좎? ?앹꽦) */
    @PostMapping("/users")
    public CommonResponse<Void> submit(@Valid @RequestBody SubmitRequest submitRequest) {
        submitRequest.check();
        submitService.submit(submitRequest);
        return new CommonResponse<>(SUCCESS_CODE, "User registered successfully", null);
    }

    /* ?뚯뀥 ?뚯썝媛??(異붽? ?뺣낫 ?낅젰) */
    @PostMapping("/users/social")
    public CommonResponse<Void> socialSubmit(@Valid @RequestBody SocialSignupRequest socialSignupRequest,
            Authentication authentication) {
        socialSignupRequest.check();
        String email = authentication.getName();
        submitService.socialSubmit(email, socialSignupRequest);
        return new CommonResponse<>(SUCCESS_CODE, "Social user updated successfully", null);
    }

}
