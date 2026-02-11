package com.couchping.service;

import com.couchping.entity.User;
import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;
import com.couchping.model.LoginRequest;
import com.couchping.repository.UserRepository;
import com.couchping.security.TokenDto;
import com.couchping.security.TokenProvider;
import com.couchping.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REDIS_REFRESH_TOKEN_PREFIX = "RT:";
    private static final String REDIS_BLACKLIST_PREFIX = "BL:";

    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        // 1. 유저 정보 조회
        User user = userRepository.findByEmail(loginRequest.email());
        if (user == null) {
            throw new CouchPingException(UserErrorCode.USER_NOT_FOUND);
        }

        // 2. 소셜 로그인 유저인지 확인 (비밀번호 없음)
        if (user.getPassword() == null) {
            throw new CouchPingException(UserErrorCode.SOCIAL_LOGIN_USER);
        }

        // 3. 비밀번호 일치 확인
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CouchPingException(UserErrorCode.PASSWORD_NOT_VALID);
        }

        // 4. 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(user.getEmail(), user.getRole().getKey());

        // 5. Redis에 Refresh Token 저장
        redisUtil.setData(REDIS_REFRESH_TOKEN_PREFIX + user.getEmail(), tokenDto.getRefreshToken(),
                refreshTokenExpiration);

        log.info("User logged in: {}", user.getEmail());

        return tokenDto;
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Refresh Token에서 email 추출
        String email = tokenProvider.getSubject(refreshToken);

        // 3. Redis에서 저장된 Refresh Token 확인
        String savedRefreshToken = redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            // 저장된 토큰이 없거나 다르면 에러 (로그아웃 처리됨 혹은 유효하지 않음)
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 4. 유저 존재 여부 확인 (DB 조회)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CouchPingException(UserErrorCode.USER_NOT_FOUND);
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(email, user.getRole().getKey());

        // 6. Redis 업데이트
        redisUtil.setData(REDIS_REFRESH_TOKEN_PREFIX + email, tokenDto.getRefreshToken(), refreshTokenExpiration);

        return tokenDto;
    }

    @Transactional
    public void logout(String accessToken) {
        // 1. Access Token 검증
        if (!tokenProvider.validateToken(accessToken)) {
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Access Token에서 User email 조회
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // 3. Redis에 저장된 Refresh Token 삭제
        if (redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email) != null) {
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
        }

        // 4. Access Token 블랙리스트에 추가 (남은 유효시간 동안)
        Long expiration = tokenProvider.getExpiration(accessToken);
        Long now = new Date().getTime();
        redisUtil.setData(REDIS_BLACKLIST_PREFIX + accessToken, "logout", expiration - now);
    }
}
