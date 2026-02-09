package com.roomhub.model;

import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;

import java.util.Map;

public record RegisterTermRequest(String title,
        String version,
        boolean required,
        boolean active,
        String createdBy,
        String updatedBy) implements ValidationCheck {

    @Override
    public void check() {
        // 약관 정보 확인
        if (this == null)
            throw new RoomHubException(UserErrorCode.TERM_IS_EMPTY);
        // 제목
        if (title == null || title.isEmpty())
            throw new RoomHubException(UserErrorCode.TERM_TITLE_IS_EMPTY);
        // 버전
        if (version == null)
            throw new RoomHubException(UserErrorCode.VERSION_IS_EMPTY);
        // createdBy
        if (createdBy == null)
            throw new RoomHubException(UserErrorCode.CREATEDBY_IS_EMPTY);
        // updatedBy
        if (createdBy == null)
            throw new RoomHubException(UserErrorCode.UPDATEDBY_IS_EMPTY);
    }
}
