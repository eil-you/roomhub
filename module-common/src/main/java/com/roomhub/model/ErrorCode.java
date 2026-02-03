package com.roomhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
public enum ErrorCode implements BaseErrorCode {

    EMAIL_NOT_VALID(1001, "Email not valid", HttpStatus.BAD_REQUEST),
    EMAIL_FROM_IS_EMPTY(1002, "Email from is empty", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_VALID(1003, "Password not valid", HttpStatus.UNAUTHORIZED),
    PASSWORD_FROM_NOT_VALID(1004, "Password from not valid", HttpStatus.BAD_REQUEST),
    GENDER_IS_EMPTY(1005, "Gender is empty", HttpStatus.BAD_REQUEST),
    BIRTH_NOT_VALID(1006, "Birth not valid", HttpStatus.BAD_REQUEST),
    NICKNAME_IS_EMPTY(1007, "Nickname is empty", HttpStatus.BAD_REQUEST),
    TERMIDS_IS_EMPTY(1008, "TermIds is empty", HttpStatus.BAD_REQUEST),
    ENCRYPTED_KEY_IS_EMPTY(1009, "Encrypted key is empty", HttpStatus.BAD_REQUEST),
    CODE_NOT_VALID(1010, "Code not valid", HttpStatus.BAD_REQUEST),
    RESEND_COOLDOWN(1011, "Code resend is not allowed within 3 minutes", HttpStatus.TOO_MANY_REQUESTS),
    TERM_IS_EMPTY(1016, "Term is empty", HttpStatus.BAD_REQUEST),
    VERSION_IS_EMPTY(1017, "Version is empty", HttpStatus.BAD_REQUEST),
    TERM_TITLE_IS_EMPTY(1018, "Term title is empty", HttpStatus.BAD_REQUEST),
    CREATEDBY_IS_EMPTY(1019, "CreatedBy is empty", HttpStatus.BAD_REQUEST),
    UPDATEDBY_IS_EMPTY(1020, "UpdatedBy is empty", HttpStatus.BAD_REQUEST),
    LOCKVERSION_IS_EMPTY(1021, "LockVersion is empty", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_IS_EMPTY(1022, "Phone number is empty", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_NOT_VALID(1023, "Phone number not valid", HttpStatus.BAD_REQUEST),
    REQUIRED_TERMS_CHECKED(1024, "Required terms must be checked", HttpStatus.BAD_REQUEST),
    PHONENUMBER_COOLDOWN(1025, "Phone number is not allowed within 1 hour", HttpStatus.TOO_MANY_REQUESTS),
    NICKNAME_IS_DUPLICATE(1026, "Nickname is duplicate", HttpStatus.CONFLICT),
    CODE_NOT_MATCHED(1027, "Code not matched", HttpStatus.BAD_REQUEST),
    CODE_EXPIRED(1028, "Code expired", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED(1029, "Already registered User", HttpStatus.CONFLICT),
    CODE_IS_EMPTY(1030, "Code not found", HttpStatus.NOT_FOUND),
    PHONENUMBER_FAILED_ENCRYPT(1031, "Phone number failed to encrypt", HttpStatus.INTERNAL_SERVER_ERROR),
    PHONENUMBER_FAILED_DECRYPT(1032, "Phone number failed to decrypt", HttpStatus.INTERNAL_SERVER_ERROR),
    CODE_FAILD_COUNT_LIMIT(1033, "Code failed count limit", HttpStatus.TOO_MANY_REQUESTS),
    USER_NOT_FOUND(1034, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_IS_EMPTY(1035, "Password is empty", HttpStatus.BAD_REQUEST),
    SOCIAL_LOGIN_USER(1036, "This user must login through social login", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    @NonNull
    private final HttpStatus httpStatus;

}
