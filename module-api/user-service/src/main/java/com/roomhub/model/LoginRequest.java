package com.roomhub.model;

import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;
import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) implements ValidationCheck {

    @Override
    public void check() {
        if (email == null || email.isBlank()) {
            throw new RoomHubException(UserErrorCode.EMAIL_FROM_IS_EMPTY);
        }
        if (password == null || password.isBlank()) {
            throw new RoomHubException(UserErrorCode.PASSWORD_IS_EMPTY);
        }
    }
}
