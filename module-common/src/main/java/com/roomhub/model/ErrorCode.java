package com.roomhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EMAIL_NOT_VALID(1001, "Email not valid"),
    EMAIL_FROM_IS_EMPTY(1002, "Email from is empty"),
    PASSWORD_NOT_VALID(1003, "Password not valid"),
    PASSWORD_FROM_NOT_VALID(1004, "Password from not valid"),
    GENDER_IS_EMPTY(1005, "Gender is empty"),
    BIRTH_NOT_VALID(1006, "Birth not valid"),
    NICKNAME_IS_EMPTY(1007, "Nickname is empty"),
    TERMIDS_IS_EMPTY(1008, "TermIds is empty"),
    ENCRYPTED_KEY_IS_EMPTY(1009, "Encrypted key is empty"),
    CODE_NOT_VALID(1010, "Code not valid"),
    RESEND_COOLDOWN(1011, "Code resend is not allowed within 3 minutes"),
    TERM_IS_EMPTY(1016, "Term is empty"),
    VERSION_IS_EMPTY(1017, "Version is empty"),
    TERM_TITLE_IS_EMPTY(1018, "Term title is empty"),
    CREATEDBY_IS_EMPTY(1019, "CreatedBy is empty"),
    UPDATEDBY_IS_EMPTY(1020, "UpdatedBy is empty"),
    LOCKVERSION_IS_EMPTY(1021, "LockVersion is empty"),
    PHONE_NUMBER_IS_EMPTY(1022, "Phone number is empty"),
    PHONE_NUMBER_NOT_VALID(1023, "Phone number not valid"),
    REQUIRED_TERMS_CHECKED(1024, "Required termsId is checked"),
    PHONENUMBER_COOLDAWN(1025, "Phone number is not allowed within 1 hour"),
    NICKNAME_IS_DUPLICATE(1026, "Nickname is duplicate"),
    CODE_NOT_MATCHED(1027, "Code not matched"),
    CODE_EXPRIRED(1028, "Code expired"),
    ALREADY_REGISTERED(1029, "Already registered User"),
    CODE_IS_EMPTY(1030, "Code not found"),
    PHONENUMBER_FALID_ENCRYPT(1031, "Phone number failed to encrypt"),
    CODE_IS_NOT_MATCHED(1032, "Code is not matched"),
    CODE_FAILD_COUNT_LIMIT(1033, "Code failed count limit"),
    USER_NOT_FOUND(1034, "User not found"),
    PASSWORD_IS_EMPTY(1035, "Password is empty"),
    SOCIAL_LOGIN_USER(1036, "This user must login through social login");

    private int code;
    private String message;

}
