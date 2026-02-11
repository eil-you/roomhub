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
        // 1. ??李??곗쨮 ?醫? 鈺곌퀬??
        User user = userRepository.findByEmail(loginRequest.email());
        if (user == null) {
            throw new CouchPingException(UserErrorCode.USER_NOT_FOUND);
        }

        // 2. ????嚥≪뮄????醫??紐? ?類ㅼ뵥 (??λ뮞???굡 ??곸벉)
        if (user.getPassword() == null) {
            throw new CouchPingException(UserErrorCode.SOCIAL_LOGIN_USER);
        }

        // 3. ??쑬?甕곕뜇????깊뒄 ?類ㅼ뵥
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CouchPingException(UserErrorCode.PASSWORD_NOT_VALID);
        }

        // 4. ?醫뤾쿃 ??밴쉐
        TokenDto tokenDto = tokenProvider.generateTokenDto(user.getEmail(), user.getRole().getKey());

        // 5. Redis ??Refresh Token ????
        redisUtil.setData(REDIS_REFRESH_TOKEN_PREFIX + user.getEmail(), tokenDto.getRefreshToken(),
                refreshTokenExpiration);

        log.info("User logged in: {}", user.getEmail());

        return tokenDto;
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 1. Refresh Token 野꺜筌?
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Refresh Token ?癒?퐣 email ?곕뗄??
        String email = tokenProvider.getSubject(refreshToken);

        // 3. Redis ?癒?퐣 ???貫留?Refresh Token ?類ㅼ뵥
        String savedRefreshToken = redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            // ?醫뤾쿃 ??沅??獄???쑴????臾롫젏 ??Redis ?怨쀬뵠??????(癰귣똻釉?揶쏅벤??
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 4. ?醫? ?類ｋ궖 獄?亦낅슦釉??類ㅼ뵥 (DB 鈺곌퀬??
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CouchPingException(UserErrorCode.USER_NOT_FOUND);
        }

        // 5. ??덉쨮???醫뤾쿃 ??밴쉐
        TokenDto tokenDto = tokenProvider.generateTokenDto(email, user.getRole().getKey());

        // 6. Redis ??낅쑓??꾨뱜
        redisUtil.setData(REDIS_REFRESH_TOKEN_PREFIX + email, tokenDto.getRefreshToken(), refreshTokenExpiration);

        return tokenDto;
    }

    @Transactional
    public void logout(String accessToken) {
        // 1. Access Token 野꺜筌?
        if (!tokenProvider.validateToken(accessToken)) {
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);
        }

        // 2. Access Token ?癒?퐣 User email ??揶쎛?紐꾩긾
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        // 3. Redis ?????貫留?Refresh Token ????
        if (redisUtil.getData(REDIS_REFRESH_TOKEN_PREFIX + email) != null) {
            redisUtil.deleteData(REDIS_REFRESH_TOKEN_PREFIX + email);
        }

        // 4. Access Token ?됰뗀?볡뵳????筌ｌ꼶??(??? ?醫륁뒞??볦퍢 ??덈툧 ????
        Long expiration = tokenProvider.getExpiration(accessToken);
        Long now = new Date().getTime();
        redisUtil.setData(REDIS_BLACKLIST_PREFIX + accessToken, "logout", expiration - now);
    }
}

