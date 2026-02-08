package com.roomhub.model;

import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;
import com.roomhub.util.ValidationUtil;
import lombok.Data;

import java.util.Map;

@Data
public class VerifyRequest implements ValidationCheck {
    private String phoneNumber;
    private String verificationCode;

    @Override
    public void check() {
        if (verificationCode.length() != 4 || verificationCode.isEmpty() || verificationCode == null)
            throw new RoomHubException(UserErrorCode.CODE_NOT_VALID);

        for (char c : verificationCode.toCharArray()) {
            if (!Character.isDigit(c))
                throw new RoomHubException(UserErrorCode.CODE_NOT_VALID, Map.of("verificationCode", verificationCode));
        }

    }
}
