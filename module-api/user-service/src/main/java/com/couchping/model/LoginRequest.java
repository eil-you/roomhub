package com.couchping.model;

import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;
import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) implements ValidationCheck {

    @Override
    public void check() {
        if (email == null || email.isBlank()) {
            throw new CouchPingException(UserErrorCode.EMAIL_FROM_IS_EMPTY);
        }
        if (password == null || password.isBlank()) {
            throw new CouchPingException(UserErrorCode.PASSWORD_IS_EMPTY);
        }
    }
}

