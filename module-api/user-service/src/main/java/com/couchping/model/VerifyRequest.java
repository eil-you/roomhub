package com.couchping.model;

import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;
import com.couchping.util.ValidationUtil;
import lombok.Data;

import java.util.Map;

@Data
public class VerifyRequest implements ValidationCheck {
    private String phoneNumber;
    private String verificationCode;

    @Override
    public void check() {
        if (verificationCode.length() != 4 || verificationCode.isEmpty() || verificationCode == null)
            throw new CouchPingException(UserErrorCode.CODE_NOT_VALID);

        for (char c : verificationCode.toCharArray()) {
            if (!Character.isDigit(c))
                throw new CouchPingException(UserErrorCode.CODE_NOT_VALID, Map.of("verificationCode", verificationCode));
        }

    }
}

