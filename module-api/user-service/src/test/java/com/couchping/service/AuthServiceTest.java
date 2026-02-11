package com.couchping.service;

import com.couchping.entity.User;
import com.couchping.exception.CouchPingException;
import com.couchping.model.Role;
import com.couchping.repository.UserRepository;
import com.couchping.security.TokenDto;
import com.couchping.security.TokenProvider;
import com.couchping.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@example.com";
    private final String refreshToken = "test-refresh-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void refresh_success() {
        // given
        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);

        TokenDto newTokenDto = TokenDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getSubject(refreshToken)).thenReturn(email);
        when(redisUtil.getData("RT:" + email)).thenReturn(refreshToken);
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(tokenProvider.generateTokenDto(email, Role.USER.getKey())).thenReturn(newTokenDto);

        // when
        TokenDto result = authService.refresh(refreshToken);

        // then
        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        verify(redisUtil).setData(eq("RT:" + email), eq("new-refresh-token"), anyLong());
    }

    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰 삭제 및 블랙리스트 등록")
    void logout_success() {
        // given
        String accessToken = "test-access-token";
        long expiration = System.currentTimeMillis() + 3600000L;

        org.springframework.security.core.Authentication auth = mock(
                org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);

        when(tokenProvider.validateToken(accessToken)).thenReturn(true);
        when(tokenProvider.getAuthentication(accessToken)).thenReturn(auth);
        when(tokenProvider.getExpiration(accessToken)).thenReturn(expiration);
        when(redisUtil.getData("RT:" + email)).thenReturn(refreshToken);

        // when
        authService.logout(accessToken);

        // then
        verify(redisUtil).deleteData("RT:" + email);
        verify(redisUtil).setData(startsWith("BL:"), eq("logout"), anyLong());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        com.couchping.model.LoginRequest request = new com.couchping.model.LoginRequest(email, "password");
        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setPassword("encodedPassword");

        TokenDto tokenDto = TokenDto.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenProvider.generateTokenDto(anyString(), anyString())).thenReturn(tokenDto);

        // when
        TokenDto result = authService.login(request);

        // then
        assertNotNull(result);
        assertEquals("access", result.getAccessToken());
        verify(redisUtil).setData(eq("RT:" + email), eq("refresh"), anyLong());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void login_fail_wrong_password() {
        // given
        com.couchping.model.LoginRequest request = new com.couchping.model.LoginRequest(email, "wrong");
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThrows(CouchPingException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 재발급 실패")
    void refresh_fail_invalid_token() {
        // given
        when(tokenProvider.validateToken(refreshToken)).thenReturn(false);

        // when & then
        assertThrows(CouchPingException.class, () -> authService.refresh(refreshToken));
    }
}
