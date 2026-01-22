package com.roomhub.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TermId implements Serializable {
    private String title;
    private String version;

    // transaction 병리
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TermId termId = (TermId) o;
        return title.equals(termId.title) && version.equals(termId.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, version);
    }

}
