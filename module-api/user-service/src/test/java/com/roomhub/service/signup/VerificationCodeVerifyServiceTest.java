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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeVerifyServiceTest {

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationCodePolicyService verificationCodePolicyService;

    @InjectMocks
    private VerificationCodeVerifyService verificationCodeVerifyService;

    private MockedStatic<AES256Util> aes256UtilMockedStatic;

    private final String phoneNumber = "01012345678";
    private final String code = "1234";
    private final String secretKey = "test-secret-key-123456789012345678";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(verificationCodeVerifyService, "secretKey", secretKey);
        aes256UtilMockedStatic = mockStatic(AES256Util.class);
    }

    @AfterEach
    void tearDown() {
        if (aes256UtilMockedStatic != null) {
            aes256UtilMockedStatic.close();
        }
    }

    @Test
    @DisplayName("인증번호 검증 성공 테스트")
    void verifyCode_success() throws Exception {
        // given
        VerifyRequest request = new VerifyRequest();
        request.setPhoneNumber(phoneNumber);
        request.setVerificationCode(code);

        Verification vc = new Verification();
        vc.setPhoneNumber(phoneNumber);
        vc.setCode(code);
        vc.setFailCount(0);

        when(verificationRepository.findVerificationByPhoneNumber(phoneNumber)).thenReturn(Optional.of(vc));
        when(verificationCodePolicyService.isVerificationCodeValid(vc)).thenReturn(true);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);
        when(AES256Util.encrypt(anyString(), anyString())).thenReturn("encrypted-phone-key");

        // when
        VerifyResponse response = verificationCodeVerifyService.verifyCode(request);

        // then
        assertNotNull(response);
        assertEquals("encrypted-phone-key", response.getEncryptedKey());
        assertTrue(vc.isUsed());
        verify(verificationRepository).save(vc);
    }

    @Test
    @DisplayName("잘못된 인증번호 입력 시 failCount 증가 테스트")
    void verifyCode_fail_wrong_code() {
        // given
        VerifyRequest request = new VerifyRequest();
        request.setPhoneNumber(phoneNumber);
        request.setVerificationCode("wrong");

        Verification vc = new Verification();
        vc.setPhoneNumber(phoneNumber);
        vc.setCode(code);
        vc.setFailCount(0);

        when(verificationRepository.findVerificationByPhoneNumber(phoneNumber)).thenReturn(Optional.of(vc));
        when(verificationCodePolicyService.isVerificationCodeValid(vc)).thenReturn(true);

        // when
        // Note: The logic in incrementFailCount is called if !isCodeMatch
        // and isAlreadyRegistered is also checked.
        assertThrows(RoomHubException.class, () -> verificationCodeVerifyService.verifyCode(request));

        // then
        assertEquals(1, vc.getFailCount());
        verify(verificationRepository).save(vc);
    }

    @Test
    @DisplayName("이미 가입된 번호일 경우 예외 발생 테스트")
    void verifyCode_fail_already_registered() {
        // given
        VerifyRequest request = new VerifyRequest();
        request.setPhoneNumber(phoneNumber);
        request.setVerificationCode(code);

        Verification vc = new Verification();
        vc.setPhoneNumber(phoneNumber);
        vc.setCode(code);

        when(verificationRepository.findVerificationByPhoneNumber(phoneNumber)).thenReturn(Optional.of(vc));
        when(verificationCodePolicyService.isVerificationCodeValid(vc)).thenReturn(true);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(new User());

        // when & then
        RoomHubException exception = assertThrows(RoomHubException.class,
                () -> verificationCodeVerifyService.verifyCode(request));
        assertEquals(ErrorCode.ALREADY_REGISTERED.getCode(), exception.getCode());
    }
}
