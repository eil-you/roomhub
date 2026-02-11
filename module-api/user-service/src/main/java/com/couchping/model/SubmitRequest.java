package com.couchping.model;

import com.couchping.entity.TermId;
import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;
import com.couchping.util.ValidationUtil;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;

public record SubmitRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull @Past LocalDate birth,
        @NotNull Gender gender,
        @NotBlank String nickname,
        @NotEmpty List<TermId> termIds,
        @NotBlank String encryptedKey) implements ValidationCheck {

    @Override
    public void check() {
        if (this == null)
            throw new CouchPingException(UserErrorCode.EMAIL_FROM_IS_EMPTY);
        // ??李??野꺜??
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || !email.matches(regex))
            throw new CouchPingException(UserErrorCode.EMAIL_NOT_VALID, Map.of("email", email));

        // ??쑬? 甕곕뜇??筌ｋ똾寃?
        ValidationUtil.checkPassword(password);
        // gender
        if (gender == null)
            throw new CouchPingException(UserErrorCode.GENDER_IS_EMPTY);
        // birth
        if (birth == null || birth.isAfter(LocalDate.now()))
            throw new CouchPingException(UserErrorCode.BIRTH_NOT_VALID, Map.of("birth", birth));
        // nickname
        if (nickname == null)
            throw new CouchPingException(UserErrorCode.NICKNAME_IS_EMPTY);
        // terms
        if (termIds == null || termIds.size() == 0)
            throw new CouchPingException(UserErrorCode.TERMIDS_IS_EMPTY);
        // encryptedKey
        if (encryptedKey == null)
            throw new CouchPingException(UserErrorCode.ENCRYPTED_KEY_IS_EMPTY);

    }
}

