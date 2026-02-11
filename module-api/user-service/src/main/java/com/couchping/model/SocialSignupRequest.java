package com.couchping.model;

import com.couchping.entity.TermId;
import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SocialSignupRequest(
        @NotNull @Past LocalDate birth,
        @NotNull Gender gender,
        @NotBlank String nickname,
        @NotEmpty List<TermId> termIds,
        @NotBlank String encryptedKey) implements ValidationCheck {

    @Override
    public void check() {
        if (gender == null)
            throw new CouchPingException(UserErrorCode.GENDER_IS_EMPTY);
        if (birth == null || birth.isAfter(LocalDate.now()))
            throw new CouchPingException(UserErrorCode.BIRTH_NOT_VALID, Map.of("birth", birth));
        if (nickname == null)
            throw new CouchPingException(UserErrorCode.NICKNAME_IS_EMPTY);
        if (termIds == null || termIds.isEmpty())
            throw new CouchPingException(UserErrorCode.TERMIDS_IS_EMPTY);
        if (encryptedKey == null)
            throw new CouchPingException(UserErrorCode.ENCRYPTED_KEY_IS_EMPTY);
    }
}

