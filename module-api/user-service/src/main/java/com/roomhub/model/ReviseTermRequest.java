package com.roomhub.model;

import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;

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
            throw new RoomHubException(ErrorCode.TERM_IS_EMPTY);
        if (version == null)
            throw new RoomHubException(ErrorCode.VERSION_IS_EMPTY);
        if (updatedBy == null)
            throw new RoomHubException(ErrorCode.UPDATEDBY_IS_EMPTY);
        if (lockVersion == null)
            throw new RoomHubException(ErrorCode.LOCKVERSION_IS_EMPTY);
    }
}
