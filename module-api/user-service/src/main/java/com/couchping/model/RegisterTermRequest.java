package com.couchping.model;

import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;

import java.util.Map;

public record RegisterTermRequest(String title,
        String version,
        boolean required,
        boolean active,
        String createdBy,
        String updatedBy) implements ValidationCheck {

    @Override
    public void check() {
        // null 체크
        if (this == null)
            throw new CouchPingException(UserErrorCode.TERM_IS_EMPTY);
        // 제목
        if (title == null || title.isEmpty())
            throw new CouchPingException(UserErrorCode.TERM_TITLE_IS_EMPTY);
        // 버전
        if (version == null)
            throw new CouchPingException(UserErrorCode.VERSION_IS_EMPTY);
        // 생성자
        if (createdBy == null)
            throw new CouchPingException(UserErrorCode.CREATEDBY_IS_EMPTY);
        // 수정자
        if (createdBy == null)
            throw new CouchPingException(UserErrorCode.UPDATEDBY_IS_EMPTY);
    }
}
