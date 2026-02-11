package com.couchping.service.signup;

import com.couchping.entity.TermId;
import com.couchping.entity.User;
import com.couchping.entity.Verification;
import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;
import com.couchping.model.Gender;
import com.couchping.model.Role;
import com.couchping.model.SocialSignupRequest;
import com.couchping.repository.AgreementRepository;
import com.couchping.repository.TermRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitServiceTest {

        @Mock
        private UserRepository userRepository;
        @Mock
        private VerificationRepository verificationRepository;
        @Mock
        private TermRepository termRepository;
        @Mock
        private AgreementRepository agreementRepository;
        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private SubmitService submitService;

        private MockedStatic<AES256Util> aes256UtilMockedStatic;

        private final String email = "social@example.com";
        private final String phoneNumber = "01012345678";
        private final String encryptedKey = "encrypted-phone-key";
        private final String secretKey = "test-secret-key-123456789012345678";

        @BeforeEach
        void setUp() {
                ReflectionTestUtils.setField(submitService, "secretKey", secretKey);
                aes256UtilMockedStatic = mockStatic(AES256Util.class);
        }

        @AfterEach
        void tearDown() {
                aes256UtilMockedStatic.close();
        }

        @Test
        @DisplayName("소셜 회원가입 정보 입력 성공")
        void socialSubmit_success() throws Exception {
                // given
                TermId mandatoryTerm = new TermId("ServicePolicy", "v1");
                SocialSignupRequest request = new SocialSignupRequest(
                                LocalDate.of(1995, 1, 1),
                                Gender.MALE,
                                "newNickname",
                                List.of(mandatoryTerm),
                                encryptedKey);

                User guestUser = new User();
                guestUser.setEmail(email);
                guestUser.setRole(Role.GUEST);

                when(AES256Util.decrypt(anyString(), eq(encryptedKey))).thenReturn(phoneNumber);
                when(userRepository.findByNickname(request.nickname())).thenReturn(null);

                Verification verification = new Verification();
                verification.setSendTime(LocalDateTime.now());
                when(verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, true))
                                .thenReturn(Optional.of(verification));

                when(termRepository.findAllByRequiredAndActive(true, true)).thenReturn(List.of(mandatoryTerm));
                when(userRepository.findByEmail(email)).thenReturn(guestUser);

                // when
                submitService.socialSubmit(email, request);

                // then
                assertEquals(Role.USER, guestUser.getRole());
                assertEquals("newNickname", guestUser.getNickname());
                assertEquals(phoneNumber, guestUser.getPhoneNumber());
                verify(userRepository).save(guestUser);
                verify(agreementRepository).saveAll(any());
        }

        @Test
        @DisplayName("중복된 닉네임으로 소셜 회원가입 실패")
        void socialSubmit_fail_duplicate_nickname() throws Exception {
                // given
                SocialSignupRequest request = new SocialSignupRequest(
                                LocalDate.now(), Gender.MALE, "duplicate", List.of(), encryptedKey);

                when(AES256Util.decrypt(anyString(), anyString())).thenReturn(phoneNumber);
                when(userRepository.findByNickname("duplicate")).thenReturn(new User());

                // when & then
                CouchPingException exception = assertThrows(CouchPingException.class,
                                () -> submitService.socialSubmit(email, request));
                assertEquals(UserErrorCode.NICKNAME_IS_DUPLICATE.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("인증되지 않은 전화번호로 소셜 회원가입 실패")
        void socialSubmit_fail_unverified_phone() throws Exception {
                // given
                SocialSignupRequest request = new SocialSignupRequest(
                                LocalDate.now(), Gender.MALE, "nick", List.of(), encryptedKey);

                when(AES256Util.decrypt(anyString(), anyString())).thenReturn(phoneNumber);
                when(userRepository.findByNickname(anyString())).thenReturn(null);
                when(verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, true))
                                .thenReturn(Optional.empty());

                // when & then
                CouchPingException exception = assertThrows(CouchPingException.class,
                                () -> submitService.socialSubmit(email, request));
                assertEquals(UserErrorCode.PHONENUMBER_COOLDOWN.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 소셜 회원가입 실패")
        void socialSubmit_fail_user_not_found() throws Exception {
                // given
                SocialSignupRequest request = new SocialSignupRequest(
                                LocalDate.now(), Gender.MALE, "nick", List.of(), encryptedKey);

                when(AES256Util.decrypt(anyString(), anyString())).thenReturn(phoneNumber);
                when(userRepository.findByNickname(anyString())).thenReturn(null);

                Verification verification = new Verification();
                verification.setSendTime(LocalDateTime.now());
                when(verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, true))
                                .thenReturn(Optional.of(verification));

                when(termRepository.findAllByRequiredAndActive(true, true)).thenReturn(Collections.emptyList());
                when(userRepository.findByEmail(email)).thenReturn(null);

                // when & then
                CouchPingException exception = assertThrows(CouchPingException.class,
                                () -> submitService.socialSubmit(email, request));
                assertEquals(UserErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
        }

        @Test
        @DisplayName("일반 회원가입 성공")
        void submit_success() throws Exception {
                // given
                TermId mandatoryTerm = new TermId("ServicePolicy", "v1");
                com.couchping.model.SubmitRequest request = new com.couchping.model.SubmitRequest(
                                "test@example.com",
                                "Password123!",
                                LocalDate.of(1995, 1, 1),
                                com.couchping.model.Gender.MALE,
                                "testNickname",
                                List.of(mandatoryTerm),
                                encryptedKey);

                when(AES256Util.decrypt(anyString(), eq(encryptedKey))).thenReturn(phoneNumber);
                when(userRepository.findByEmail(anyString())).thenReturn(null);
                when(userRepository.findByNickname(anyString())).thenReturn(null);

                Verification verification = new Verification();
                verification.setSendTime(LocalDateTime.now());
                when(verificationRepository.findVerificationByPhoneNumberAndUsed(phoneNumber, true))
                                .thenReturn(Optional.of(verification));

                when(termRepository.findAllByRequiredAndActive(true, true)).thenReturn(List.of(mandatoryTerm));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

                User savedUser = new User();
                ReflectionTestUtils.setField(savedUser, "id", 1L);
                savedUser.setEmail("test@example.com");

                when(userRepository.save(any(User.class))).thenReturn(savedUser);

                // when
                submitService.submit(request);

                // then
                verify(userRepository).save(any(User.class));
                verify(agreementRepository).saveAll(any());
                verify(passwordEncoder).encode("Password123!");
        }
}
