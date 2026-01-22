package com.roomhub.util;

import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValidationUtil {
    private static final List<String> validAreaCode = Arrays.asList("010", "011", "016", "017", "018", "019");

    /* 비밀번호 유효성 검사 */
    public static void checkPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RoomHubException(ErrorCode.PASSWORD_NOT_VALID, Map.of("password", password));
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        if (!password.matches(regex)) {
            throw new RoomHubException(ErrorCode.PASSWORD_FROM_NOT_VALID, Map.of("password", password));
        }
    }

    /* 전화번호 유효성 검사 */
    public static void checkPhoneNumber(String phoneNumber) {

        if (phoneNumber == null)
            throw new RoomHubException(ErrorCode.PHONE_NUMBER_IS_EMPTY, Map.of("phoneNumber", phoneNumber));

        phoneNumber = phoneNumber.trim();
        String areaCode = phoneNumber.substring(0, 3);
        if (phoneNumber.length() != 11 || !validAreaCode.contains(areaCode))
            throw new RoomHubException(ErrorCode.PHONE_NUMBER_NOT_VALID, Map.of("phoneNumber", phoneNumber));

    }

}
