package com.roomhub.service;

import com.roomhub.entity.User;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.UserErrorCode;
import com.roomhub.model.LoginRequest;
import com.roomhub.repository.UserRepository;
import com.roomhub.security.TokenDto;
import com.roomhub.security.TokenProvider;
import com.roomhub.util.RedisUtil;
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
        // 1. 이메일로 유저 조회
        User user = userRepository.findByEmail(loginRequest.email());
        if (user == null) {
            throw new RoomHubException(UserErrorCode.USER_NOT_FOUND);
        }

        // 2. 소셜 로그인 유저인지 확인 (패스워드 없음)
        if (user.getPassword() == null) {
            throw new RoomHubException(UserErrorCode.SOCIAL_LOGIN_USER);
        }

        // 3. 비밀번호 일치 확인
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RoomHubException(UserErrorCode.PASSWORD_NOT_VALID);
        }

        // 4. 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(user.getEmail(), user.getRole().getKey());

        // 5. Redis 에 Refresh Token 저장
        redisUtil.setData(REDIS_REFRESH_TOKEN_PREFIX + user.getEmail(), tokenDto.getRefreshToken(),
                refreshTokenExpiration);

        log.info("User logged in: {}", user.getEmail());

        return tokenDto;
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RoomHubException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Refresh Token 에서 email 추출
        String email = tokenProvider.getSubject(refreshToken);

        // 3. Redis 에서 저장된 Refresh Token 확인
        String savedRefreshToken = redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            // 토큰 재사용 및 비정상 접근 시 Redis 데이터 삭제 (보안 강화)
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
            throw new RoomHubException(UserErrorCode.CODE_NOT_VALID);
        }

        // 4. 유저 정보 및 권한 확인 (DB 조회)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RoomHubException(UserErrorCode.USER_NOT_FOUND);
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
            throw new RoomHubException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Access Token 에서 User email 을 가져옴
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // 3. Redis 에 저장된 Refresh Token 삭제
        if (redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email) != null) {
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
        }

        // 4. Access Token 블랙리스트 처리 (남은 유효시간 동안 저장)
        Long expiration = tokenProvider.getExpiration(accessToken);
        Long now = new Date().getTime();
        redisUtil.setData(REDIS_BLACKLIST_PREFIX + accessToken, "logout", expiration - now);
    }
}
