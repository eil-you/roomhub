package com.roomhub.model;

import com.roomhub.entity.TermId;
import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;
import com.roomhub.util.ValidationUtil;
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
            throw new RoomHubException(ErrorCode.EMAIL_FROM_IS_EMPTY);
        // 이메일 검사
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || !email.matches(regex))
            throw new RoomHubException(ErrorCode.EMAIL_NOT_VALID, Map.of("email", email));

        // 비밀 번호 체크
        ValidationUtil.checkPassword(password);
        // gender
        if (gender == null)
            throw new RoomHubException(ErrorCode.GENDER_IS_EMPTY);
        // birth
        if (birth == null || birth.isAfter(LocalDate.now()))
            throw new RoomHubException(ErrorCode.BIRTH_NOT_VALID, Map.of("birth", birth));
        // nickname
        if (nickname == null)
            throw new RoomHubException(ErrorCode.NICKNAME_IS_EMPTY);
        // terms
        if (termIds == null || termIds.size() == 0)
            throw new RoomHubException(ErrorCode.TERMIDS_IS_EMPTY);
        // encryptedKey
        if (encryptedKey == null)
            throw new RoomHubException(ErrorCode.ENCRYPTED_KEY_IS_EMPTY);

    }
}
