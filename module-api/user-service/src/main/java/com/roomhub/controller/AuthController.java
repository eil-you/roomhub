package com.roomhub.controller;

import com.roomhub.model.CommonResponse;
import com.roomhub.security.TokenDto;
import com.roomhub.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<TokenDto> login(@Valid @RequestBody com.roomhub.model.LoginRequest loginRequest) {
        loginRequest.check();
        TokenDto tokenDto = authService.login(loginRequest);
        return new CommonResponse<>(0, "Successfully logged in", tokenDto);
    }

    @PostMapping("/refresh")
    public CommonResponse<TokenDto> refresh(@RequestBody String refreshToken) {
        TokenDto tokenDto = authService.refresh(refreshToken);
        return new CommonResponse<>(0, null, tokenDto);
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        authService.logout(accessToken);
        return new CommonResponse<>(0, "Successfully logged out", null);
    }
}
