package com.roomhub.service;

import com.roomhub.model.ErrorCode;
import com.roomhub.security.TokenDto;
import com.roomhub.security.TokenProvider;
import com.roomhub.util.RedisUtil;
import com.roomhub.exception.RoomHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final com.roomhub.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public TokenDto login(com.roomhub.model.LoginRequest loginRequest) {
        // 1. 이메일로 유저 조회
        com.roomhub.entity.User user = userRepository.findByEmail(loginRequest.email());
        if (user == null) {
            throw new RoomHubException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 소셜 로그인 유저인지 확인 (패스워드 없음)
        if (user.getPassword() == null) {
            throw new RoomHubException(ErrorCode.SOCIAL_LOGIN_USER);
        }

        // 3. 비밀번호 일치 확인
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RoomHubException(ErrorCode.PASSWORD_NOT_VALID);
        }

        // 4. 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(user.getEmail(), user.getRole().getKey());

        // 5. Redis 에 Refresh Token 저장
        redisUtil.setData("RT:" + user.getEmail(), tokenDto.getRefreshToken(), refreshTokenExpiration);

        log.info("User logged in: {}", user.getEmail());

        return tokenDto;
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RoomHubException(ErrorCode.CODE_NOT_VALID);
        }

        // 2. Refresh Token 에서 email 추출
        String email = tokenProvider.getSubject(refreshToken);

        // 3. Redis 에서 저장된 Refresh Token 확인
        String savedRefreshToken = redisUtil.getData("RT:" + email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            // 토큰 재사용 및 비정상 접근 시 Redis 데이터 삭제 (보안 강화)
            redisUtil.deleteData("RT:" + email);
            throw new RoomHubException(ErrorCode.CODE_NOT_VALID);
        }

        // 4. 유저 정보 및 권한 확인 (DB 조회)
        com.roomhub.entity.User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RoomHubException(ErrorCode.USER_NOT_FOUND);
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(email, user.getRole().getKey());

        // 6. Redis 업데이트
        redisUtil.setData("RT:" + email, tokenDto.getRefreshToken(), refreshTokenExpiration);

        return tokenDto;
    }

    @Transactional
    public void logout(String accessToken) {
        // 1. Access Token 검증
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RoomHubException(ErrorCode.CODE_NOT_VALID);
        }

        // 2. Access Token 에서 User email 을 가져옴
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // 3. Redis 에 저장된 Refresh Token 삭제
        if (redisUtil.getData("RT:" + email) != null) {
            redisUtil.deleteData("RT:" + email);
        }

        // 4. Access Token 블랙리스트 처리 (남은 유효시간 동안 저장)
        Long expiration = tokenProvider.getExpiration(accessToken);
        Long now = new java.util.Date().getTime();
        redisUtil.setData("BL:" + accessToken, "logout", expiration - now);
    }
}
