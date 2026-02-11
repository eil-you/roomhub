package com.couchping.model;

import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;

public record ReviseTermRequest(
        String title,
        String version,
        boolean required,
        boolean active,
        String updatedBy,
        Long lockVersion) implements ValidationCheck {

    // todo jsr301 @Min(0)

    @Override
    public void check() {
        if (title == null)
            throw new CouchPingException(UserErrorCode.TERM_IS_EMPTY);
        if (version == null)
            throw new CouchPingException(UserErrorCode.VERSION_IS_EMPTY);
        if (updatedBy == null)
            throw new CouchPingException(UserErrorCode.UPDATEDBY_IS_EMPTY);
        if (lockVersion == null)
            throw new CouchPingException(UserErrorCode.LOCKVERSION_IS_EMPTY);
    }
}

