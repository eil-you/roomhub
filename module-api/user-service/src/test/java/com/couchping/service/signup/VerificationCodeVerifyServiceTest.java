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
    @DisplayName("인증 코드 확인 성공")
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
    @DisplayName("잘못된 인증 코드 입력 시 failCount 증가")
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
        assertThrows(CouchPingException.class, () -> verificationCodeVerifyService.verifyCode(request));

        // then
        assertEquals(1, vc.getFailCount());
        verify(verificationRepository).save(vc);
    }

    @Test
    @DisplayName("이미 가입된 전화번호로 인증 시 실패")
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
        CouchPingException exception = assertThrows(CouchPingException.class,
                () -> verificationCodeVerifyService.verifyCode(request));
        assertEquals(UserErrorCode.ALREADY_REGISTERED.getCode(), exception.getCode());
    }
}
