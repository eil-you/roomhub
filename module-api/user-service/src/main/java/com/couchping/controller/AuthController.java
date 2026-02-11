package com.couchping.controller;

import com.couchping.model.CommonResponse;
import com.couchping.model.LoginRequest;
import com.couchping.security.TokenDto;
import com.couchping.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final int SUCCESS_CODE = 0;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        loginRequest.check();
        TokenDto tokenDto = authService.login(loginRequest);
        return new CommonResponse<>(SUCCESS_CODE, "Successfully logged in", tokenDto);
    }

    @PostMapping("/refresh")
    public CommonResponse<TokenDto> refresh(@RequestBody String refreshToken) {
        TokenDto tokenDto = authService.refresh(refreshToken);
        return new CommonResponse<>(SUCCESS_CODE, null, tokenDto);
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(HttpServletRequest request) {
        String accessToken = resolveToken(request);
        if (accessToken != null) {
            authService.logout(accessToken);
        }
        return new CommonResponse<>(SUCCESS_CODE, "Successfully logged out", null);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
