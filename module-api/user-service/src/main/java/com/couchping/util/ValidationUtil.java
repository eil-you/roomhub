package com.couchping.util;

import com.couchping.exception.CouchPingException;
import com.couchping.model.UserErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValidationUtil {
    private static final List<String> validAreaCode = Arrays.asList("010", "011", "016", "017", "018", "019");

    /* 鍮꾨?踰덊샇 ?좏슚??寃??*/
    public static void checkPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new CouchPingException(UserErrorCode.PASSWORD_NOT_VALID, Map.of("password", password));
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        if (!password.matches(regex)) {
            throw new CouchPingException(UserErrorCode.PASSWORD_FROM_NOT_VALID, Map.of("password", password));
        }
    }

    /* ?꾪솕踰덊샇 ?좏슚??寃??*/
    public static void checkPhoneNumber(String phoneNumber) {

        if (phoneNumber == null)
            throw new CouchPingException(UserErrorCode.PHONE_NUMBER_IS_EMPTY, Map.of("phoneNumber", phoneNumber));

        phoneNumber = phoneNumber.trim();
        String areaCode = phoneNumber.substring(0, 3);
        if (phoneNumber.length() != 11 || !validAreaCode.contains(areaCode))
            throw new CouchPingException(UserErrorCode.PHONE_NUMBER_NOT_VALID, Map.of("phoneNumber", phoneNumber));

    }

}

